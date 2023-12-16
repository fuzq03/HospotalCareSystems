package com.xyx.travelingshare.alipay

import android.text.TextUtils


class PayResult(rawResult: String) {
    /**	 * @return the resultStatus	  */
    var resultStatus: String? = null

    /**	 * @return the result	  */
    var result: String? = null

    /**	 * @return the memo	  */
    var memo: String? = null

    init {
        val resultParams = rawResult.split(";".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (resultParam in resultParams) {
            if (resultParam.startsWith("resultStatus")) {
                resultStatus = gatValue(resultParam, "resultStatus")
            }
            if (resultParam.startsWith("result")) {
                result = gatValue(resultParam, "result")
            }
            if (resultParam.startsWith("memo")) {
                memo = gatValue(resultParam, "memo")
            }
        }
    }

    override fun toString(): String {
        return "resultStatus={$resultStatus};memo={$memo};result={$result}"
    }

    private fun gatValue(content: String, key: String): String {
        val prefix = "$key={"
        return content.substring(content.indexOf(prefix) + prefix.length, content.lastIndexOf("}"))
    }
}