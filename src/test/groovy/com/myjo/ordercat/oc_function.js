/**
 * 判断外部供应商编码
 * @param a
 * @returns {*}
 */
function judgeFilterOuterId(outerId) {

    var rt = false;
    if(outerId.indexOf('麦巨')>-1){
        rt =  true;
    }
    if(outerId.indexOf('临时')>-1){
        rt =  true;
    }
    return rt;
}