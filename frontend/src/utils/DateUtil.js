// from: https://blog.csdn.net/chw0629/article/details/80868058
//  对Date的扩展，将 Date 转化为指定格式的String
//  月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
//  年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
//  例子：
//  (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
//  (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format =  function (fmt="yyyy-MM-dd hh:mm:ss") {  // author: meizz
    var o = {
        "M+":  this.getMonth() + 1,  // 月份
        "d+":  this.getDate(),  // 日
        "h+":  this.getHours(),  // 小时
        "m+":  this.getMinutes(),  // 分
        "s+":  this.getSeconds(),  // 秒
        "q+": Math.floor(( this.getMonth() + 3) / 3),  // 季度
        "S":  this.getMilliseconds()  // 毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, ( this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for ( var k  in o)
        if ( new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

Date.prototype.addSeconds = function (second=0) {
    second = parseFloat(second)
    this.setSeconds(this.getSeconds() + second)
    return this;
}
Date.prototype.addHours = function (hours=0) {
    hours = parseFloat(hours)
    this.setHours(this.getHours() + hours)
    return this;
}
Date.prototype.addDay = function (day=0) {
    day = parseFloat(day)
    this.setDate(this.getDate() + day)
    return this;
}
Date.prototype.addMonth = function (month=0) {
    month = parseFloat(month)
    this.setMonth(this.getMonth() + month)
    return this;
}
Date.prototype.addYear = function (year=0) {
    year = parseFloat(year)
    this.setFullYear(this.getFullYear() + year)
    return this;
}

/**
* 生成新的日期
* */
Date.prototype.addSecondsReturnNew = function (second=0) {
    second = parseFloat(second)
    let tmp = new Date(this.getTime());
    tmp.setSeconds(tmp.getSeconds() + second)
    return tmp;
}
Date.prototype.addHoursReturnNew = function (hours=0) {
    hours = parseFloat(hours)
    let tmp = new Date(this.getTime());
    tmp.setHours(tmp.getHours() + hours)
    return tmp;
}
Date.prototype.addDayReturnNew = function (day=0) {
    day = parseFloat(day)
    let tmp = new Date(this.getTime());
    tmp.setDate(tmp.getDate() + day)
    return tmp;
}
Date.prototype.addMonthReturnNew = function (month=0) {
    month = parseFloat(month)
    let tmp = new Date(this.getTime());
    tmp.setMonth(tmp.setMonth() + month)
    return tmp;
}
Date.prototype.addYearReturnNew = function (year=0) {
    year = parseFloat(year)
    let tmp = new Date(this.getTime());
    tmp.setFullYear(tmp.getFullYear() + year)
    return tmp;
}

/**
 * 清除小时、分钟、秒数
 * @returns {Date}
 */
Date.prototype.clearhhmmss = function () {
    this.setHours(0)
    this.setMinutes(0)
    this.setSeconds(0)
    this.setMilliseconds(0)
    return this;
}
/**
 * 跳转这周的星期几
 * @param day 0~6
 * @returns {Date}
 */
Date.prototype.setDay = function (day) {
    let nDay = day - this.getDay();
    this.setDate(this.getDate() + nDay);
    return this;
}

Number.prototype.TimeDifferenceFormat = function (dwSizeStr = ["毫秒", "秒", "分钟", "小时", "天"]){
    let dwSize = [1000, 60, 60, 24]

    let tmp = this
    let i = 0;
    for (i = 0; i < dwSize.length; i++) {
        if(tmp >= dwSize[i]){
            tmp = tmp / dwSize[i]
        }else{
            break
        }
    }
    return tmp.toFixed(2) + " " + dwSizeStr[i]
}

window.sumDomHeight = function (dom) {
    let make = function (iii, char) {
        let tmp = "";
        for (let i = 0; i < iii; i++) {
            tmp += char
        }
        return tmp
    }
    let ii = 1
    let buff = dom
    console.log("->height:", buff.clientHeight)
    while (buff.parentElement){
        console.log(make(ii,"-")+"->height:", buff.parentElement.clientHeight, buff.parentElement)
        buff = buff.parentElement
        ii++
    }
}
window.sumDomWidth = function (dom) {
    let make = function (iii, char) {
        let tmp = "";
        for (let i = 0; i < iii; i++) {
            tmp += char
        }
        return tmp
    }
    let ii = 1
    let buff = dom
    console.log("->height:", buff.clientWidth)
    while (buff.parentElement){
        console.log(make(ii,"-")+"->width:", buff.parentElement.clientWidth, buff.parentElement)
        buff = buff.parentElement
        ii++
    }
}

window.numberSmail = function (val) {
    if(typeof val != "number"){
        return val
    }

    let dwSize = [1000, 10, 10000]
    let dwSizeStr = ["", "千", "万", "亿"]

    let tmp = val
    let i = 0;
    for (i = 0; i < dwSize.length; i++) {
        if(tmp >= dwSize[i]){
            tmp = tmp / dwSize[i]
        }else{
            break
        }
    }
    return Math.round(tmp*100)/100 + dwSizeStr[i]
}