package com.didiglobal.knowframework.observability.conponent.spring.filter;

import com.alibaba.fastjson.JSON;
import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.common.constant.Constant;
import com.didiglobal.knowframework.observability.common.util.MDCUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.*;

/**
 * 可观察拦截器 高优先级 优先处理
 *
 * @author slhu update:2023-09-26
 */
@Component
@WebFilter(urlPatterns = "/", filterName = "observabilityFilter")
public class ObservabilityFilter implements Ordered, Filter {

    private static final TextMapPropagator TEXT_MAP_PROPAGATOR = Observability.getTextMapPropagator();
    private static final Tracer TRACER = Observability.getTracer(ObservabilityFilter.class.getName());

    private static final Set<Integer> VALID_HTTP_STATUS_CODE_SET = new HashSet<>();

    static {
        VALID_HTTP_STATUS_CODE_SET.add(HttpStatus.SC_OK);
    }

    /*
     * extract the context from http headers
     */
    private static final TextMapGetter<HttpServletRequest> GETTER =
            new TextMapGetter<HttpServletRequest>() {
                @Override
                public Iterable<String> keys(HttpServletRequest carrier) {
                    List<String> iterable = new ArrayList<>();
                    Enumeration<String> enumeration = carrier.getHeaderNames();
                    while (enumeration.hasMoreElements()) {
                        String headerName = enumeration.nextElement();
                        iterable.add(headerName);
                    }
                    return iterable;
                }
                @Override
                public String get(HttpServletRequest carrier, String key) {
                    String headerValue = carrier.getHeader(key);
                    return headerValue == null ? StringUtils.EMPTY : headerValue;
                }
            };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Context context = TEXT_MAP_PROPAGATOR.extract(Context.current(), request, GETTER);
        String requestUri = request.getRequestURI();
        Span span = TRACER.spanBuilder(
                String.format("%s.%s", this.getClass().getName(), "doFilter")
        ).setParent(context).setSpanKind(SpanKind.SERVER).startSpan();
        MDCUtil.putSpan(span);
        try (Scope scope = span.makeCurrent()) {
            // Set the Semantic Convention
            span.setAttribute(Constant.ATTRIBUTE_KEY_COMPONENT, Constant.ATTRIBUTE_VALUE_COMPONENT_HTTP);
            span.setAttribute(Constant.ATTRIBUTE_KEY_HTTP_METHOD, request.getMethod());
            /*
             One of the following is required:
             - http.scheme, http.host, http.target
             - http.scheme, http.server_name, net.host.port, http.target
             - http.scheme, net.host.name, net.host.port, http.target
             - http.url
            */
            span.setAttribute(Constant.ATTRIBUTE_KEY_HTTP_SCHEMA, Constant.ATTRIBUTE_VALUE_COMPONENT_HTTP);
            span.setAttribute(Constant.ATTRIBUTE_KEY_HTTP_HOST, String.format("%s:%d", request.getLocalAddr(), request.getLocalPort()));
            span.setAttribute(Constant.ATTRIBUTE_KEY_HTTP_TARGET, requestUri);
            // Process the request
            StatusExposingServletResponse response = new StatusExposingServletResponse((HttpServletResponse) servletResponse);
            filterChain.doFilter(servletRequest, response);
            //set span status
            int httpStatus = response.getStatus();
            if (!VALID_HTTP_STATUS_CODE_SET.contains(httpStatus)) {
                span.setStatus(
                        StatusCode.ERROR,
                        String.format(
                                "http状态码%d不在合法http状态码集%s内",
                                httpStatus,
                                JSON.toJSONString(VALID_HTTP_STATUS_CODE_SET)
                        )
                );
            } else {
                span.setStatus(StatusCode.OK);
            }
        } catch (Exception ex) {
            span.setStatus(StatusCode.ERROR, ex.getMessage());
        } finally {
            // Close the span
            span.end();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    static class StatusExposingServletResponse extends HttpServletResponseWrapper implements HttpServletResponse {
        private int status = 200;
        public StatusExposingServletResponse(HttpServletResponse response) {
            super(response);
        }
        @Override
        public void setStatus(int sc) {
            this.status = sc;
            super.setStatus(sc);
        }
        @Override
        public int getStatus() {
            return status;
        }
    }

}
