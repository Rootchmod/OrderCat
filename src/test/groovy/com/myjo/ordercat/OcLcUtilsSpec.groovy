package com.myjo.ordercat

import com.myjo.ordercat.exception.OCException
import com.myjo.ordercat.utils.OcLcUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

import java.util.function.Consumer

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class OcLcUtilsSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(OcLcUtilsSpec.class);



    def "OcLcUtils.getPickRate"() {
        when:

        try{

            throw new OCException("里是是");
        }catch(Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            System.out.println("tt:"+errors.toString())
        }


        String dd = OcLcUtils.getPickRate("配货率：87%<br/>发货时效:11小时").toPlainString()
        String dd1 = OcLcUtils.getPickRate("配货率：0%<br/>发货时效:0小时").toPlainString()
        then:
        dd == "87"
        dd1 == "0"

    }


    def "OcLcUtils.cartesianProduct"() {
        when:


        String[] quarter = [
            "17Q4","17Q3","17Q2","17Q1",
            "16Q4","16Q3","16Q2","16Q1",
            "15Q4","15Q3","15Q2","15Q1",
            "14Q4","14Q3","14Q2","14Q1"
        ];

        String[] sex = [
            "男","女","中"
        ];

        String[] division = [
            "鞋","服","配"
        ];

        List<String> list1 = Arrays.asList(quarter);
        List<String> list2 = Arrays.asList(sex);
        List<String> list3 = Arrays.asList(division);
        List<List<String>> lists = Arrays.asList(list1,list2,list3);
        List<List<String>> resultLists = cartesianProduct(lists);

        resultLists.parallelStream().forEach({

            System.out.println(it)

        })
        then:
        "ok" == "ok"

    }


    public static <T>  List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<List<T>>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }



}
