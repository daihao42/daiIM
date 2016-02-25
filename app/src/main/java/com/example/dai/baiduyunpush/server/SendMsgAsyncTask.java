package com.example.dai.baiduyunpush.server;

import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;

import com.example.dai.baiduyunpush.component.MyApplication;

/**
 * Created by dai on 2016/1/15.
 */
public class SendMsgAsyncTask {
    private BaiduPush mBaiduPush;
    private String mMessage;
    private Handler mHandler;
    private MyAsyncTask mTask;
    private String mUserId;
    private OnSendScuessListener mListener;

    public interface OnSendScuessListener
    {
        void sendScuess();
    }

    public void setOnSendScuessListener(OnSendScuessListener listener)
    {
        this.mListener = listener;
    }

    Runnable reSend = new Runnable()
    {

        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            //L.i("resend msg...");
            send();
        }
    };

    public SendMsgAsyncTask(String jsonMsg, String useId)
    {
        // TODO Auto-generated constructor stub
        mBaiduPush = MyApplication.getInstance().getBaiduPush();
        mMessage = jsonMsg;
        mUserId = useId;
        mHandler = new Handler();
    }

    //发送
    public void send()
    {
        mTask = new MyAsyncTask();
        mTask.execute();
    }

    public void stop()
    {
        if (mTask != null)
            mTask.cancel(true);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, String>
    {

        @Override
        protected String doInBackground(Void... message)
        {
            String result = "";
            if (TextUtils.isEmpty(mUserId))
                return null;
            else
                result = mBaiduPush.PushMessage(mMessage, mUserId);
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result.contains(BaiduPush.SEND_MSG_ERROR))
            {
                mHandler.postDelayed(reSend, 100);
            } else
            {
                if (mListener != null)
                    mListener.sendScuess();
            }
        }
    }
}
