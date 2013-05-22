package org.daniel.android.workbanch.util;

/**
 * 用于ViewGroup中控件显示顺序调整的类。使用时先调用ViewGroup.setChildrenDrawingOrderEnabled(true)，
 * 然后覆盖getChildDrawingOrder(int childCount, int i)，方法，用这个类的相应方法生成返回值
 * 
 * @author 焦阳 <br>
 *         email:yangjiao623@gmail.com
 * @version 创建时间: May 22, 2013 10:52:02 AM
 * */
public class DrawOrderHelper {
    //
    private int[] orders = null;

    public DrawOrderHelper(int size) {
        reset(size);
    }

    /**
     * reset drawing order
     * */
    public void reset(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("error in size < 0");
        }
        orders = new int[size];
        int count = orders.length;
        for (int i = 0; i < count; i++) {
            orders[i] = i;
        }
    }

    /**
     * @param count
     *            the amount of children
     * @param i
     *            current drawing index and i<count
     * @return target drawing index and return < count
     * */
    public int getChildDrawingOrder(int childCount, int i) {
        if (orders.length != childCount) {
            reset(childCount);
        }
        if (isNotInArray(i)) {
            throw new IllegalArgumentException("i should be in [0,count)");
        }

        return orders[i];
    }

    public void bring2Front(int i) {
        if (isNotInArray(i)) {
            throw new IllegalArgumentException("i should be in [0,count)");
        }

        // i should be to the end to of the array
        int x = orders[i];
        int count = orders.length - 1;
        for (int o = i; o < count; o++) {
            orders[o] = orders[o + 1];
        }
        orders[count] = x;
    }

    public void put2Back(int i) {
        if (isNotInArray(i)) {
            throw new IllegalArgumentException("i should be in [0,count)");
        }

        int x = orders[i];
        for (int o = i; o > 0; o--) {
            orders[o] = orders[o - 1];
        }
        orders[0] = x;
    }

    public void exchange(int i0, int i1) {
        if (isNotInArray(i0) || isNotInArray(i1)) {
            throw new IllegalArgumentException(
                    "i0 and i1 should be in [0,count)");
        }

        int x = orders[i0];
        orders[i0] = orders[i1];
        orders[i1] = x;
    }

    private boolean isNotInArray(int i) {
        return i < 0 || i >= orders.length;
    }
}
