# 免費的中文語音辨識 API
--------------------------
本 library 把音檔以 streaming 的方式送到雲端作語音辨識，分析後回傳結果，並且支持 partial result 及 VAD，目前尚未支持對音檔結果的回覆(intension)，目前因資源有限，僅限於繁體中文的使用，不過會持續更新中，之後也會陸續提供其它功能。

### 特色
  - 台灣口音中文語音辨識
  - 即時返回辨識結果
  - 速度快且辨識率準確
  - 免費使用

# 執行環境
--------------------------
  - jdk 1.8
  - android 4 以上 

# 執行方式
--------------------------  
 1. 請到 https://aispeechcloud.online 註冊一個帳號
 2. 註冊完後會有確認信函
 3. 點擊確認網址後會得到 token，權限為一天 2000 次，無限期
 4. 匯入 jar 檔
 5. 準備取樣頻率為 16k 的音檔, 可以是 pcm 或 wav
 6. 執行 sample code 如下 (java 方法和 android 同)
  ```sh
// 假設 token = "ABCDEFG"
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import mybusiness.beans.DataBean;
import mybusiness.beans.DataResultBean;
import mybusiness.beans.ResponseBean;
import mybusiness.beans.ValueBean;
import mybusiness.util.RecognitionClient;
import mybusiness.util.RecognitionClientListener;

public class Test {
	
	public static void main(String[] args) throws IOException{
		RecognitionClient client = new RecognitionClient("aispeechcloud.online/Service/RecognitionService",new MyRecognitionClientListener(),true);  
	    //設定逾時(1/1000秒)
		RecognitionClient.setTimeout(20000);
		
		//設定token
		ValueBean bean = new ValueBean();
		bean.setKey("token"); //不用動
		bean.setValue("ABCDEFG");  //TOKEN放這裡
		
		//連線
		client.connection(bean);
		
		FileInputStream in = new FileInputStream(new File("d://test.pcm"));
		try{
			try{
				byte buffer[] = new byte[6400];
				int length;
				while((length = in.read(buffer)) != -1){
		           	DataBean data = new DataBean();
		           	data.setData(buffer.clone());
		           	data.setDataLength(length);
		           	data.setEnd(false);
	      		           	
		           	client.submit(data);
	            		            	
	            	System.out.println("SEND...................");
	       	    }
		        DataBean data = new DataBean();
		        data.setEnd(true);

	       		client.submit(data);
	       		
			}catch(Exception e){
				e.printStackTrace();
			}
		}finally{
			in.close();
		}
		
		try {
			//等答案回來
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static class MyRecognitionClientListener implements RecognitionClientListener{
		//已連線
		@Override
		public void onOpen(Session session, EndpointConfig EndpointConfig){
			
		}
		
		//回傳訊息
		@Override
		public void onMessage(DataResultBean bean){
			//最後結果
			if(bean.isEnd()){
				System.out.println("last " + bean.getResult());
			//PartialResult
                        }else{
			    System.out.println(bean.getResult());
			}
		}

		//回傳回應訊息
		@Override
		public void onResponse(ResponseBean bean){
			System.out.println(bean.getResponse());;
		}
		
		//錯誤處理
		@Override
		public void onError(Session session, Throwable thr){
			thr.printStackTrace();
		}

		//關閉連線
		@Override
		public void onClose(Session session, CloseReason closeReason){
			System.out.println("close...");
		}
	}	
}
  ```
 7. 會從 onMessage method 得到結果如下
 ```sh
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
SEND...................
//回傳結果
{"status":"ok","data":[{"confidence":0.960668,"likelihood":51.9952,"frames":21,"ac_cost":-66.5165,"lm_cost":14.5214,"text":""}]}
{"status":"ok","data":[{"confidence":0.959442,"likelihood":105.769,"frames":42,"ac_cost":-148.135,"lm_cost":42.3665,"text":"台北市"}]}
last {"status":"ok","data":[{"confidence":0.95941,"likelihood":222.658,"frames":99,"ac_cost":-316.041,"lm_cost":93.3831,"text":"台北市 內湖區 瑞光路"}]}
{"msg":""}  //保留回應用
close...
client closed...
```

**如果有任何問題均可寄信至 aispeechcloud@gmail.com 詢問或上論壇討論 https://groups.google.com/forum/#!forum/ai-speech-cloud**

