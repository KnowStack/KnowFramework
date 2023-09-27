package com.didiglobal.knowframework.observability.conponent.mybatis;

import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.common.constant.Constant;
import com.didiglobal.knowframework.observability.common.util.MDCUtil;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author slhu
 */
@Slf4j
@Intercepts(
        {
                @Signature(
                        type = Executor.class,
                        method = "query",
                        args = {
                                MappedStatement.class,
                                Object.class,
                                RowBounds.class,
                                ResultHandler.class
                        }
                ),
                @Signature(
                        type = Executor.class,
                        method = "update",
                        args = {
                                MappedStatement.class,
                                Object.class
                        }
                )
        }
)
@Component
public class ObservabilityInterceptor implements Interceptor {

    private final Tracer tracer = Observability.getTracer(ObservabilityInterceptor.class.getName());

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        String clazzName = invocation.getTarget().getClass().getName();
        String methodName = invocation.getMethod().getName();
        Span span = tracer.spanBuilder(String.format("%s.%s", clazzName, methodName)).startSpan();
        MDCUtil.putSpan(span);
        try (Scope scope = span.makeCurrent()) {
            // 根据签名指定的args顺序获取具体的实现类
            // 1. 获取MappedStatement实例, 并获取当前SQL命令类型
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            SqlCommandType commandType = ms.getSqlCommandType();
            // 2. 获取当前正在被操作的类, 有可能是Java Bean, 也可能是普通的操作对象, 比如普通的参数传递
            // 普通参数, 即是 @Param 包装或者原始 Map 对象, 普通参数会被 Mybatis 包装成 Map 对象
            // 即是 org.apache.ibatis.binding.MapperMethod$ParamMap
            Object parameter = invocation.getArgs()[1];
            // 3. 获取对应 sql 语 句
            String sql = ms.getBoundSql(parameter).getSql();
            span.setAttribute(Constant.ATTRIBUTE_KEY_SQL_STATEMENT, sql);
            span.setAttribute(Constant.ATTRIBUTE_KEY_SQL_TYPE, commandType.name());
            Object result = invocation.proceed();
            span.setStatus(StatusCode.OK);
            return result;
        } catch (Throwable ex) {
            span.setStatus(StatusCode.ERROR, ex.getMessage());
            throw ex;
        } finally {
            MDCUtil.removeSpanId(span);
            span.end();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // process
    }

}