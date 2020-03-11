package com.example.mywatchface

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

class EngineHandler(reference: MyWatchFace.Engine) : Handler() {
    private val mWeakReference: WeakReference<MyWatchFace.Engine> = WeakReference(reference)

    override fun handleMessage(msg: Message) {
        mWeakReference.get().let {
            if (msg.what == MSG_UPDATE_TIME) {
                it?.handleUpdateTimeMessage()
            }
        }
    }
}