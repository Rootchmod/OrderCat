package com.myjo.ordercat.utils;

import com.myjo.ordercat.domain.Brand;
import com.myjo.ordercat.domain.Sex;
import com.myjo.ordercat.domain.Size;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lee5hx on 17/4/26.
 */
public class OcSizeUtils {

    private static Map<Sex, List<Size>> shoeSizeMap = new HashMap();


    private static Map<String,String> clothesSizeMap = new HashMap();


    static {


        clothesSizeMap.put("XL","XL");
        clothesSizeMap.put("XXL","XXL");
        clothesSizeMap.put("XXXL","XXXL");
        clothesSizeMap.put("XXXXL","XXXXL");
        clothesSizeMap.put("XXXXXL","XXXXXL");
        clothesSizeMap.put("1XL","XL");
        clothesSizeMap.put("2XL","XXL");
        clothesSizeMap.put("3XL","XXXL");
        clothesSizeMap.put("4XL","XXXXL");
        clothesSizeMap.put("5XL","XXXXXL");
        clothesSizeMap.put("XS","XS");
        clothesSizeMap.put("S","S");
        clothesSizeMap.put("M","M");
        clothesSizeMap.put("L","L");




        List<Size> shoeMalelist = new ArrayList();
        List<Size> shoeFemalelist = new ArrayList();

        //男鞋码                           美码(us)	英码(uk)	 中国码	厘米
        shoeMalelist.add(new Size(Sex.MALE, "6",   "5.5",  "38.5", "24"));
        shoeMalelist.add(new Size(Sex.MALE, "6.5", "6",    "39",   "24.5"));
        shoeMalelist.add(new Size(Sex.MALE, "7",   "6",    "40",   "25"));
        shoeMalelist.add(new Size(Sex.MALE, "7.5", "6.5",  "40.5", "25.5"));
        shoeMalelist.add(new Size(Sex.MALE, "8",   "7",    "41",   "26"));
        shoeMalelist.add(new Size(Sex.MALE, "8.5", "7.5",  "42",   "26.5"));
        shoeMalelist.add(new Size(Sex.MALE, "9",   "8",    "42.5", "27"));
        shoeMalelist.add(new Size(Sex.MALE, "9.5", "8.5",  "43",   "27.5"));
        shoeMalelist.add(new Size(Sex.MALE, "10",  "9",    "44",   "28"));
        shoeMalelist.add(new Size(Sex.MALE, "10.5", "9.5",  "44.5", "28.5"));
        shoeMalelist.add(new Size(Sex.MALE, "11",  "10",   "45",   "29"));
        shoeMalelist.add(new Size(Sex.MALE, "11.5", "10.5", "45.5", "29.5"));
        shoeMalelist.add(new Size(Sex.MALE, "12",  "11",   "46",   "30"));
        shoeMalelist.add(new Size(Sex.MALE, "13",  "12",   "47.5", "31"));
        shoeMalelist.add(new Size(Sex.MALE, "14",  "13",   "48.5", "32"));
        shoeMalelist.add(new Size(Sex.MALE, "15",  "14",   "49.5", "33"));
        shoeMalelist.add(new Size(Sex.MALE, "16",  "15",   "50.5", "34"));
        shoeMalelist.add(new Size(Sex.MALE, "17",  "16",   "51.5", "35"));
        shoeMalelist.add(new Size(Sex.MALE, "18",  "17",   "52.5", "36"));

        //女鞋码
        shoeFemalelist.add(new Size(Sex.FEMALE, "5",    "2.5",   "35.5", "22"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "5.5",  "3",     "36",   "22.5"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "6",    "3.5",   "36.5", "23"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "6.5",  "4",     "37.5", "23.5"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "7",    "4.5",   "38",   "24"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "7.5",  "5",     "38.5", "24.5"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "8",    "5.5",   "39",   "25"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "8.5",  "6",     "40",   "25.5"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "9",    "6.5",   "40.5", "26"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "9.5",  "7",     "41",   "26.5"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "10",   "7.5",   "42",   "27"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "10.5", "8",     "42.5", "27.5"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "11",   "8.5",   "43",   "28"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "11.5", "9",     "44",   "28.5"));
        shoeFemalelist.add(new Size(Sex.FEMALE, "12",   "9.5",   "44.5", "29"));

        shoeSizeMap.put(Sex.MALE, shoeMalelist);
        shoeSizeMap.put(Sex.FEMALE, shoeFemalelist);
    }

    /**
     * 鞋码换算
     * @param brand
     * @param sex
     * @param size2
     * @return
     */
    public static String getShoeSize1BySize2(Brand brand, Sex sex, String size2) {
        String rt = "";
        List<Size> shoeSizelist = shoeSizeMap.get(sex);
        for(Size s :shoeSizelist){
            if(brand == Brand.NIKE){
                if(s.getUS().equals(size2.trim())){
                    rt = s.getEurope();
                    break;
                }
            }
        }
        return rt;
    }

    public static String getClothesConversionSize1(String size1) {
        if(clothesSizeMap.get(size1) == null){
            return "error_size["+size1+"]";
        }else {
            return clothesSizeMap.get(size1);
        }
    }


}
