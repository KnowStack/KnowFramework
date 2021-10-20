package com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.KeyWord;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.Node;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.key.KeyNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.multi.NodeList;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.multi.NodeMap;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.JsonNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.ObjectNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.QueryStringValueNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.common.value.StringListNode;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.query.QueryString;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.FieldDataFields;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.Fields;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.ast.root.Source;
import com.didiglobal.logi.elasticsearch.client.parser.dsl.visitor.basic.OutputVisitor;
import com.didiglobal.logi.elasticsearch.client.parser.query_string.visitor.QSFormatVisitor;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;

import java.util.*;

public class FormatVisitor extends OutputVisitor {

    private static final ILog LOGGER = LogFactory.getLog(FormatVisitor.class);

    @Override
    public void visit(Source node) {
        this.ret = "[?]";
    }

    @Override
    public void visit(Fields node) {
        this.ret = "?";
    }

    @Override
    public void visit(ObjectNode node) {
        ret = "?";
    }

    @Override
    public void visit(FieldDataFields node) {
        ret = "[?]";
    }

    @Override
    public void visit(QueryString node) {

        JSONObject root = new JSONObject(true);
        Map<String, Object> sortedMap = new TreeMap<>();


        if (node.n instanceof NodeMap) {
            Node valueNode = null;
            NodeMap nodeMap = (NodeMap)node.n;

            for (KeyNode n : nodeMap.m.keySet()) {
                n.accept(this);
                String key = (String) ret;
                valueNode = nodeMap.m.get(n);


                if ("query".equalsIgnoreCase(key) && valueNode instanceof StringListNode) {

                    sortedMap.put(key, "?");

                } else if ("fields".equalsIgnoreCase(key)) {
                    sortedMap.put(key, "[?]");

                } else if ("default_field".equalsIgnoreCase(key)) {
                    sortedMap.put(key, "?");

                } else {
                    valueNode.accept(this);
                    Object value = ret;
                    sortedMap.put(key, value);
                }
            }
        }

        root.putAll(sortedMap);
        this.ret = root;
    }

    @Override
    public void visit(QueryStringValueNode node) {
        QSFormatVisitor formatVisitor = new QSFormatVisitor();
        node.getQsNode().accept(formatVisitor);
        ret = formatVisitor.output();
    }


    @Override
    public void visit(NodeMap node) {

        JSONObject root = new JSONObject(true);

        Map<String, Object> sortedMap = new TreeMap<>();
        for (KeyNode n : node.m.keySet()) {
            n.accept(this);
            String key = (String) ret;

            node.m.get(n).accept(this);
            Object value = ret;
            sortedMap.put(key, value);
        }

        root.putAll(sortedMap);
        this.ret = root;
    }


    @Override
    public void visit(NodeList node) {
        List<Object> l = new ArrayList<>();

        for (Node n : node.l) {
            if (n instanceof KeyWord) {
                JSONObject obj = new JSONObject();
                n.accept(this);
                obj.put(((KeyWord) n).getName(), ret);
            } else {
                n.accept(this);
            }

            l.add(ret);
        }


        List<Object> tmp = new ArrayList<>();
        Set<String> exist = new HashSet<>();
        for (Object obj : l) {
            String str = JSON.toJSONString(obj);
            if (exist.contains(str)) {
                continue;
            } else {
                tmp.add(obj);
                exist.add(str);
            }
        }

        l = tmp;
        Collections.sort(l, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 == o2) {
                    return 0;
                }

                if (o1 == null) {
                    return -1;
                }

                if (o2 == null) {
                    return 1;
                }

                return o1.toString().compareTo(o2.toString());
            }
        });

        JSONArray array = new JSONArray();
        for (Object o : l) {
            array.add(o);
        }

        ret = array;
    }

    public void visit(JsonNode node) {
        JSON json = clone(node.json);

        if (json instanceof JSONObject) {
            doJsonObject((JSONObject) json);
        }

        if (node.json instanceof JSONArray) {
            doJsonArray((JSONArray) json);
        }

        ret = json;
    }

    public void doJsonObject(JSONObject object) {
        for (String key : object.keySet()) {
            Object o = object.get(key);

            if (o instanceof JSONObject) {
                doJsonObject((JSONObject) o);
                continue;
            }

            if (o instanceof JSONArray) {
                doJsonArray((JSONArray) o);
                continue;
            }

            object.put(key, "?");
        }
    }

    public void doJsonArray(JSONArray array) {
        List<Object> l = new ArrayList<>();

        Set<String> exist = new HashSet<>();
        for (Object o : array) {
            if (o instanceof JSONObject) {
                doJsonObject((JSONObject) o);

            } else if (o instanceof JSONArray) {
                doJsonArray((JSONArray) o);

            } else {
                o = "?";
            }

            String str = JSON.toJSONString(o);
            if (exist.contains(str)) {
                continue;
            }

            exist.add(str);
            l.add(o);
        }


        Collections.sort(l, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                if (o1 == o2) {
                    return 0;
                }

                if (o1 == null) {
                    return -1;
                }

                if (o2 == null) {
                    return 1;
                }

                return o1.toString().compareTo(o2.toString());
            }
        });

        array.clear();
        for (Object o : l) {
            array.add(o);
        }
    }

    public JSON clone(JSON json) {
        return (JSON) JSON.parse(JSON.toJSONString(json, SerializerFeature.WriteMapNullValue));
    }

}
