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
  	RecognitionClient client = new RecognitionClient("aispeechcloud.online:80/Service/RecognitionService",new MyRecognitionClientListener(),false);  
      //設定逾時(1/1000秒)
  	RecognitionClient.setTimeout(20000);
  	
  	//設定token
  	ValueBean bean = new ValueBean();
  	bean.setKey("token"); //不用動
  	bean.setValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");  //TOKEN放這裡
  	
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