

package com.didiglobal.logi.elasticsearch.client.parser.query_string.parser;



public interface CharStream {


    char readChar() throws java.io.IOException;

    @Deprecated

    int getColumn();

    @Deprecated

    int getLine();


    int getEndColumn();


    int getEndLine();


    int getBeginColumn();


    int getBeginLine();


    void backup(int amount);


    char BeginToken() throws java.io.IOException;


    String GetImage();


    char[] GetSuffix(int len);


    void Done();
}

