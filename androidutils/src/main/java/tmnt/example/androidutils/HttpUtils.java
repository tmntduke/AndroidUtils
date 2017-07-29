package tmnt.example.androidutils;

import android.util.Log;
import android.view.SurfaceHolder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

//Http请求的工具类
public class HttpUtils
{

	private static final String TAG = "HttpUtils";
	private static final int TIMEOUT_IN_MILLIONS = 10000;

	public interface CallBack
	{
		void onRequestComplete(byte[] result);
		void onError(Exception e);
	}


	/**
	 * 异步的Get请求
	 * 
	 * @param urlStr
	 * @param callBack
	 */
	public static void doGetAsyn(final String urlStr, final CallBack callBack
		, final Map<String,Object> params, final String enconding)
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					byte[] result = doGet(urlStr,params,enconding);
					if (callBack != null)
					{
						callBack.onRequestComplete(result);
					}
				} catch (Exception e)
				{   
					if(callBack!=null){
				      callBack.onError(e);
				     }
					e.printStackTrace();
					
				}

			};
		}.start();
	}


	/**
	 * 异步的Post请求
	 * @param urlStr
	 * @param params
	 * @param callBack
	 * @throws Exception
	 */
	public static void doPostAsyn(final String urlStr, final Map<String,String> params, final String encode
			,final CallBack callBack) throws Exception
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					byte[] result = doPost(urlStr, params,encode);
					if (callBack != null)
					{
						callBack.onRequestComplete(result);
					}
				} catch (Exception e)
				{
					if(callBack!=null){
				      callBack.onError(e);
				     }
					e.printStackTrace();
				}

			};
		}.start();

	}

	/**
	 * Get请求，获得返回数据
	 * 
	 * 有参数请求时 可以有两种形式 
	 * 1. 直接在url中将参数加入
	 * 2. 用params 参数传入
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static byte[] doGet(String urlStr,Map<String,Object>params,String encoding) 
	{
		URL url = null;
		StringBuffer strBuffer=new StringBuffer(urlStr);
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
        
		if(params!=null && !params.isEmpty()){
		
		   strBuffer.append("?");
		   for(Map.Entry<String,Object>entry : params.entrySet()){
		     strBuffer.append(entry.getKey()).append("=");
			   try {
				   strBuffer.append(URLEncoder.encode(String.valueOf(entry.getValue()),encoding));
			   } catch (UnsupportedEncodingException e) {
				   e.printStackTrace();
			   }
			   strBuffer.append("&");
		   }
		   strBuffer.deleteCharAt(strBuffer.length()-1);

		}


		try
		{
			url = new URL(strBuffer.toString());
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			if (conn.getResponseCode() == 200)
			{
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[1024];

				while ((len = is.read(buf)) != -1)
				{
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toByteArray();
			} else
			{
				throw new RuntimeException(" responseCode is not 200 ... ");
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (is != null)
					is.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				if (baos != null)
					baos.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			conn.disconnect();
		}
		
		return null ;

	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param params
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static byte[] doPost(String url, Map<String,String> params,String encodeing)
	{
		PrintWriter out = null;
		ByteArrayOutputStream baos = null;
		InputStream in=null;
		OutputStream op=null;
        StringBuffer str=new StringBuffer();
		if(params!=null && !params.isEmpty()){
		   
		   for(Map.Entry<String,String>entry:params.entrySet()){
		   
		       str.append(entry.getKey()).append("=");
			   try {
				   str.append(URLEncoder.encode(String.valueOf(entry.getValue()),encodeing));
			   } catch (UnsupportedEncodingException e) {
				   e.printStackTrace();
			   }
			   str.append("&");

		   }
		   str.deleteCharAt(str.length()-1);

		}

		byte[] data1=str.toString().getBytes();



		try
		{
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			// 设置通用的请求属性
			conn.setFollowRedirects(true);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", String.valueOf(data1.length));
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

			if (params != null && !params.isEmpty())
			{
				// 获取URLConnection对象对应的输出流
				//out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				//out.print(data1);
				// flush输出流的缓冲
//				out.flush();
				op=conn.getOutputStream();
				op.write(data1);


			}
			// 定义BufferedReader输入流来读取URL的响应

			if (conn.getResponseCode()==200){
				in=conn.getInputStream();

				baos = new ByteArrayOutputStream();
				byte [] bata=new byte[1024];
				int len=0;
				while ((len=in.read(bata)) != -1)
				{
					baos.write(bata, 0, len);
				}
				baos.flush();
				conn.disconnect();
				return baos.toByteArray();
			}


			//return baos.toByteArray();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (baos != null)
				{
					baos.close();
				}
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return null;
	}
}
