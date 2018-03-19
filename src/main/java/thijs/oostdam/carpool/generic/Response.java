package thijs.oostdam.carpool.generic;

import org.apache.http.NameValuePair;

import java.util.List;

public class Response<B> {
    private B body;
    private List<NameValuePair> headers;

    public Response(B body, List<NameValuePair> headers){
        this.body = body;
        this.headers = headers;
    }

    public B getBody(){
        return body;
    }

    public List<NameValuePair> getHeaders(){
        return headers;
    }
}
