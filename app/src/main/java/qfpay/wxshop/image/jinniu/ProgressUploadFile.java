package qfpay.wxshop.image.jinniu;

import com.adhoc.http.Callback;
import com.adhoc.http.MediaType;
import com.adhoc.http.MultipartBuilder;
import com.adhoc.http.Request;
import com.adhoc.http.RequestBody;
import com.adhoc.http.Response;
import com.adhoc.io.Buffer;
import com.adhoc.io.BufferedSink;
import com.adhoc.io.Okio;
import com.adhoc.io.Source;
import com.adhoc.net.AdhocNet;

import java.io.File;
import java.io.IOException;

/**
 * Created by dongjunkun on 2015/8/31.
 */
public class ProgressUploadFile {

    public void run(String filename, String token, String url) {


        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        builder.addFormDataPart("token", token);
        builder.addFormDataPart("key", System.currentTimeMillis() + filename);

        File file = new File(filename);
        builder.addFormDataPart("file", file.getName(), createCustomRequestBody(MultipartBuilder.FORM, file, new ProgressListener() {
            @Override
            public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                System.out.print((totalBytes - remainingBytes) * 100 / totalBytes + "%");
            }
        }));



        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url).addHeader("User-Agent", com.qiniu.upload.tool.Config.USER_AGENT)//地址
                .post(requestBody)
                .build();

        AdhocNet.getInstance().enqueue(request, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("response.body().string() = " + response.body().string());

            }
        });
    }

    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final ProgressListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }

    public static void main(String[] args) {
        new ProgressUploadFile().run("filename", "token", "url");
    }

}