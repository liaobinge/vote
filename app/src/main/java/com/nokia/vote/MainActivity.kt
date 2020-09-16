package com.nokia.vote

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var joinId="715";
    var userId=0;
    var voteDelay=0;
    var voteNum=200;
    val HANDLER_VOTE=2000
    var successVote=0
    var userVoteNum=5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_start_vote.setOnClickListener {
            initVote()
            nextVote()
        }
    }
    private fun initVote(){
        joinId=et_join_id.text.toString()
        userId=0
        userVoteNum=3
        voteDelay=et_vote_delay.text.toString().toInt()
        voteNum=et_vote_number.text.toString().toInt()
    }
    private fun vote(){
        var req=JSONObject()
        req.put("join_id",joinId)
        req.put("user_id",userId)
        OkGo.post<String>("https://beauty.shengdaosoft.com/wechat/activity/userMakevoting").upJson(req)
            .execute(object :StringCallback(){
            override fun onSuccess(response: Response<String>?) {
                var jsonObject=JSONObject(response!!.body())
                if(jsonObject.has("code")&&jsonObject.getInt("code")==200){
                    success()
                }else{
                    fail()
                }
            }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    fail()
                }
        })
    }

    private fun success(){
        userVoteNum--
         successVote++
        tv_vote_tip.text="成功票数为:"+successVote
        nextVote()
    }
    private fun fail(){
        nextUserId()
       nextVote()
    }
    private fun nextUserId(){
        userId=(0..37900).random()
        userVoteNum=(1..5).random()
    }
    private fun nextVote(){
        if(userVoteNum==0){
            nextUserId()
        }
        var delay=0L
        if(voteDelay!=0){
            delay = (0L..voteDelay*1000L).random()

        }
        hander.sendEmptyMessageDelayed(HANDLER_VOTE,delay);

    }

    private var hander=object :Handler(){
        override fun dispatchMessage(msg: Message) {
            super.dispatchMessage(msg)
            when(msg.what){
                HANDLER_VOTE->{vote()}
            }
        }
    }

}
