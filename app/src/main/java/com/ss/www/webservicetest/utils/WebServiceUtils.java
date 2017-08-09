package com.ss.www.webservicetest.utils;

import android.os.Handler;
import android.os.Message;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by SS on 17-7-12.
 */
public class WebServiceUtils {
    // 访问的服务器是否由dotNet开发
    public static boolean isDotNet = false;
    public static boolean isNet = true;
    // 线程池的大小
    private static int threadSize = 5;
    // 创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程
    private static ExecutorService threadPool = Executors.newFixedThreadPool(threadSize);
    // 连接响应标示
    public static final int SUCCESS_FLAG = 0;
    public static final int ERROR_FLAG = 1;
    public static void call(final String endPoint,
                            final String nameSpace,
                            final String methodName,
                            final String str1,
                            final String str2,
                            final Response responseCallBack){
        // 1.创建HttpTransportSE对象，传递WebService服务器地址
        final HttpTransportSE transport = new HttpTransportSE(endPoint,3000);
        //transport.debug = true;
        // 2.创建SoapObject对象用于传递请求参数
        final SoapObject request = new SoapObject(nameSpace, methodName);
        //3传入参数，也可以不用传
        //request.addProperty("mobileCode ",str1);
        //request.addProperty("userID ",str2);
        request.addProperty("User ",str1);
        request.addProperty("Pwd ",str2);
        //soapheader在这里
       /* Element[] header = new Element[1];
        header[0] = new Element().createElement(nameSpace, "ServiceCredential ");
        Element username = new Element().createElement(nameSpace, "UserID");
        username.addChild(Node.TEXT, "zc_imis");
        header[0].addChild(Node.ELEMENT, username);
        Element pass = new Element().createElement(nameSpace, "Pwd");
        pass.addChild(Node.TEXT, "_!zcadmin");
        header[0].addChild(Node.ELEMENT, pass);*/
        // 4.实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = isNet; // 设置是否调用的是.Net开发的WebService
       // envelope.headerOut = header;//认证信息
        envelope.bodyOut = request;// 设置请求参数,和setOutputSoapObject()方法效果一样
        envelope.setOutputSoapObject(request);
        // 5.用于子线程与主线程通信的Handler，网络请求成功时会在子线程发送一个消息，然后在主线程上接收

        final Handler responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 根据消息的arg1值判断调用哪个接口
                if (msg.arg1 == SUCCESS_FLAG)
                    responseCallBack.onSuccess((SoapObject) msg.obj);
                else
                    responseCallBack.onError((Exception) msg.obj);

            }

        };
        // 6.提交一个子线程到线程池并在此线种内调用WebService
        if (threadPool == null || threadPool.isShutdown())
            threadPool = Executors.newFixedThreadPool(threadSize);
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                SoapObject result = null;
                try {
                    // 解决EOFException
                    System.setProperty("http.keepAlive", "false");
                    // 连接服务器
                    transport.call(null, envelope);
                    if (envelope.getResponse() != null) {
                        // 获取服务器响应返回的SoapObject
                        result = (SoapObject) envelope.bodyIn;
                    }
                } catch (IOException e) {
                    // 当call方法的第一个参数为null时会有一定的概念抛IO异常
                    // 因此需要需要捕捉此异常后用命名空间加方法名作为参数重新连接
                    e.printStackTrace();
                    try {
                        transport.call(nameSpace + methodName, envelope);
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SoapObject
                            result = (SoapObject) envelope.bodyIn;
                        }
                    } catch (Exception e1) {
                        // e1.printStackTrace();
                        responseHandler.sendMessage(responseHandler.obtainMessage(0, ERROR_FLAG, 0, e1));
                    }
                } catch (XmlPullParserException e) {
                    // e.printStackTrace();
                    responseHandler.sendMessage(responseHandler.obtainMessage(0, ERROR_FLAG, 0, e));
                } finally {
                    // 将获取的消息利用Handler发送到主线程
                    if(result != null){
                    responseHandler.sendMessage(responseHandler.obtainMessage(0, SUCCESS_FLAG, 0, result));
                     }

                }
            }
        });
    }
    public static void call2(final String endPoint,
                             final String nameSpace,
                             final String methodName,
                             final int str1,
                             final String str2,
                             final Response responseCallBack){
        // 1.创建HttpTransportSE对象，传递WebService服务器地址
        final HttpTransportSE transport = new HttpTransportSE(endPoint);
        //transport.debug = true;
        // 2.创建SoapObject对象用于传递请求参数
        final SoapObject request = new SoapObject(nameSpace, methodName);
        //3传入参数，也可以不用传
        //request.addProperty("mobileCode ",str1);
        //request.addProperty("userID ",str2);
        request.addProperty("ModularID ",str1);
        request.addProperty("MeasureTime ",str2);
        // 4.实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = isNet; // 设置是否调用的是.Net开发的WebService
        envelope.bodyOut = request;// 设置请求参数,和setOutputSoapObject()方法效果一样
        envelope.setOutputSoapObject(request);

        // 5.用于子线程与主线程通信的Handler，网络请求成功时会在子线程发送一个消息，然后在主线程上接收

        final Handler responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 根据消息的arg1值判断调用哪个接口
                if (msg.arg1 == SUCCESS_FLAG)
                    responseCallBack.onSuccess((SoapObject) msg.obj);
                else
                    responseCallBack.onError((Exception) msg.obj);
            }
        };

        // 6.提交一个子线程到线程池并在此线种内调用WebService
        if (threadPool == null || threadPool.isShutdown())
            threadPool = Executors.newFixedThreadPool(threadSize);
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                SoapObject result = null;
                try {
                    // 解决EOFException
                    System.setProperty("http.keepAlive", "false");
                    // 连接服务器
                    transport.call(null, envelope);
                    if (envelope.getResponse() != null) {
                        // 获取服务器响应返回的SoapObject
                        result = (SoapObject) envelope.bodyIn;
                    }
                } catch (IOException e) {
                    // 当call方法的第一个参数为null时会有一定的概念抛IO异常
                    // 因此需要需要捕捉此异常后用命名空间加方法名作为参数重新连接
                    e.printStackTrace();
                    try {
                        transport.call(nameSpace + methodName, envelope);
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SoapObject
                            result = (SoapObject) envelope.bodyIn;
                        }
                    } catch (Exception e1) {
                        // e1.printStackTrace();
                        responseHandler.sendMessage(responseHandler.obtainMessage(0, ERROR_FLAG, 0, e1));
                    }
                } catch (XmlPullParserException e) {
                    // e.printStackTrace();
                    responseHandler.sendMessage(responseHandler.obtainMessage(0, ERROR_FLAG, 0, e));
                } finally {
                    // 将获取的消息利用Handler发送到主线程
                    if(result != null){
                        responseHandler.sendMessage(responseHandler.obtainMessage(0, SUCCESS_FLAG, 0, result));
                    }

                }
            }
        });

    }
    public static void call3(final String endPoint,
                             final String nameSpace,
                             final String methodName,
                             final int str1,
                             final String str2,
                             final Handler handler){

        // 1.创建HttpTransportSE对象，传递WebService服务器地址
        final HttpTransportSE transport = new HttpTransportSE(endPoint);
        //transport.debug = true;
        // 2.创建SoapObject对象用于传递请求参数
        final SoapObject request = new SoapObject(nameSpace, methodName);
        //3传入参数，也可以不用传
        //request.addProperty("mobileCode ",str1);
        //request.addProperty("userID ",str2);
        request.addProperty("ModularID ",str1);
        request.addProperty("MeasureTime ",str2);
        // 4.实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.dotNet = isNet; // 设置是否调用的是.Net开发的WebService
        envelope.bodyOut = request;// 设置请求参数,和setOutputSoapObject()方法效果一样
        envelope.setOutputSoapObject(request);
        // 6.提交一个子线程到线程池并在此线种内调用WebService
        if (threadPool == null || threadPool.isShutdown())
            threadPool = Executors.newFixedThreadPool(threadSize);
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                SoapObject result = null;
                try {
                    // 解决EOFException
                    System.setProperty("http.keepAlive", "false");
                    // 连接服务器
                    transport.call(null, envelope);

                    if (envelope.getResponse() != null) {
                        // 获取服务器响应返回的SoapObject
                        result = (SoapObject) envelope.bodyIn;
                        // Log.i("main---测试1",result+"");
                    }
                } catch (IOException e) {
                    // 当call方法的第一个参数为null时会有一定的概念抛IO异常
                    // 因此需要需要捕捉此异常后用命名空间加方法名作为参数重新连接
                    e.printStackTrace();
                    try {
                        transport.call(nameSpace + methodName, envelope);
                        if (envelope.getResponse() != null) {
                            // 获取服务器响应返回的SoapObject
                            result = (SoapObject) envelope.bodyIn;
                        }
                    } catch (Exception e1) {
                        // e1.printStackTrace();
                        Message msg1 = Message.obtain();
                        msg1.what = ERROR_FLAG;
                        msg1.obj = e1;
                        handler.sendMessage(msg1);
                    }
                } catch (XmlPullParserException e) {
                    // e.printStackTrace();
                    handler.sendMessage(handler.obtainMessage(0, ERROR_FLAG, 0, e));
                } finally {
                    // 将获取的消息利用Handler发送到主线程
                    if(result != null){
                    handler.sendMessage(handler.obtainMessage(0, SUCCESS_FLAG, 0, result));
                    //Log.i("main---测试1-----",result+"");
                    Message message = Message.obtain();
                    message.what = SUCCESS_FLAG;
                    message.obj = result;
                    handler.sendMessage(message);
                     }
                }
            }

        });

    }
    public interface Response {
        void onSuccess(SoapObject result);

        void onError(Exception e);
    }
}
