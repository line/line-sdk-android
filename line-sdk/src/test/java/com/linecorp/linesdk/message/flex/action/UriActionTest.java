package com.linecorp.linesdk.message.flex.action;

import com.linecorp.linesdk.LineSdkTestRunner;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UriActionTest extends LineSdkTestRunner {

    private UriAction uriAction;

    @Test
    public void only_label() throws JSONException {
        uriAction = new UriAction(null, "View details");

        messageShouldBe("{" +
                "  \"type\": \"uri\"," +
                "  \"label\": \"View details\"" +
                "}");
    }

    @Test
    public void only_uri() throws JSONException {
        uriAction = new UriAction("https://d.line-scdn.net/n/line_lp/img/logo160629.png");

        messageShouldBe("{" +
                "  \"type\": \"uri\"," +
                "  \"uri\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "}");
    }

    @Test
    public void with_label_and_uri() throws JSONException {
        uriAction = new UriAction("https://d.line-scdn.net/n/line_lp/img/logo160629.png", "View details");

        messageShouldBe("{" +
                "  \"type\": \"uri\"," +
                "  \"label\": \"View details\"," +
                "  \"uri\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "}");
    }

    private void messageShouldBe(String expected) throws JSONException {
        assertEquals(new JSONObject(expected).toString(), uriAction.toJsonObject().toString());
    }
}