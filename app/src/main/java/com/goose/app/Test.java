package com.goose.app;

/**
 * Created by taoyr on 2018/6/14.
 */

public class Test {

    Test() {
        print(isLetter(""));
        print(isLetter("  "));
        print(isLetter(" abc"));
        //print(isLetter(null));

        print("adult | kid | infant\n");
        String template = "%d | %d | %d\n";
        for (int adult = 1; adult <= MAX_PASSENGER; adult++) {
            for (int child = 0; child <= getMaxChildPassenger(adult); child++) {
                int infant = getMaxInfantPassenger(adult, child);
                print(String.format(template, adult, child, infant));
            }
        }
    }

    void print(Object obj) {
        System.out.println(obj);
    }

    public static void main(String[] args) {
        new Test();
    }

    public static boolean isLetter(String str) {
        //return str.matches("[a-zA-Z\\s]+"); // 官网姓名允许输入空格，但会做处理，比如名含空格，会把空格后一部分拿到middle name去。John Gerard Custodio
        return str.matches("[a-zA-Z]+");
    }

    static int MAX_PASSENGER = 9;
    static int KIDS_PER_PASSENGER = 3;
    static int INFANT_PER_PASSENGER = 1;

    /**
     * 购票的业务逻辑是，先选adult，动态计算出可选的儿童数量，再选儿童（默认选择0），最后动态计算出可选择的婴儿数量
     */
    public static int getMaxChildPassenger(int adult) {
        int result = 0;
        int vacancy = MAX_PASSENGER - adult;
        if (adult * KIDS_PER_PASSENGER <= vacancy) { // 允许可携带的最大数量，在剩余的容量内
            result = adult * KIDS_PER_PASSENGER;
        } else {
            result = vacancy;
        }
        return result;
    }

    public static int getMaxInfantPassenger(int adult, int child) {
        int result = 0;
        int vacancy = MAX_PASSENGER - adult - child;
        if (adult * INFANT_PER_PASSENGER <= vacancy) {
            result = adult * INFANT_PER_PASSENGER;
        } else {
            result = vacancy;
        }

        // 儿童和婴儿的总数，不能超过特定数目成年人允许携带的最大孩子的数量
        if (result + child > adult * KIDS_PER_PASSENGER) {
            result = adult * KIDS_PER_PASSENGER - child;
        }

        return result;
    }
}
