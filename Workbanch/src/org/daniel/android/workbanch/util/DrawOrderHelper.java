package org.daniel.android.workbanch.util;

/**
 * 用于ViewGroup中控件显示顺序调整的类。使用时先调用ViewGroup.setChildrenDrawingOrderEnabled(true)，
 * 然后覆盖getChildDrawingOrder(int childCount, int i)，方法，用这个类的相应方法生成返回值<br>
 * 注意：这个类只能改变显示顺序，不能改变事件的接收顺序，也就是说，覆盖在上面的view就算显示在下面也会先接受到触摸事件
 * 
 * @author 焦阳 <br>
 *         email:jiaoyang1@staff.sina.com.cn
 * @version 创建时间: May 22, 2013 10:52:02 AM
 * */
public class DrawOrderHelper {
    // {0, 1, 2, ..., length-1}
    private int[] orders = null;

    public DrawOrderHelper(int size) {
        reset(size);
    }

    /**
     * reset drawing order
     * */
    public void reset(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("error in " + size + " < 0");
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
            throw new IllegalArgumentException("i should be in [0, "
                    + orders.length + "), in fact " + i);
        }

        return orders[i];
    }

    /**
     * @param i
     *            [0, orders.length)
     * */
    public void bring2Front(int i) {
        if (isNotInArray(i)) {
            throw new IllegalArgumentException("i should be in [0, "
                    + orders.length + "), in fact " + i);
        }

        // 寻找显示点的位置
        int index = getIndex(i);

        if (index == orders.length - 1) {
            return;
        }

        // i should be to the end to of the array
        int x = orders[index];
        System.arraycopy(orders, index + 1, orders, index, orders.length
                - index - 1);
        orders[orders.length - 1] = x;
    }

    /**
     * @param i
     *            [0, orders.length)
     * @return [0, orders.length)
     * */
    private int getIndex(int i) {
        int index = 0;
        for (; index < orders.length && orders[index] != i; index++)
            ;
        return index;
    }

    /**
     * @param i
     *            [0, orders.length)
     * */
    public void put2Back(int i) {
        if (isNotInArray(i)) {
            throw new IllegalArgumentException("i should be in [0, "
                    + orders.length + "), in fact " + i);
        }
        int index = getIndex(i);
        if (index == 0) {
            return;
        }

        int x = orders[index];
        System.arraycopy(orders, 0, orders, 1, index);
        orders[0] = x;
    }

    public void exchange(int i0, int i1) {
        if (isNotInArray(i0) || isNotInArray(i1)) {
            throw new IllegalArgumentException("i0 and i1 should be in [0,"
                    + orders.length + " ) in face i0 = " + i0 + ", i1 = " + i1);
        }

        int index0 = getIndex(i0);
        int index1 = getIndex(i1);

        int x = orders[index0];
        orders[index0] = orders[index1];
        orders[index1] = x;
    }

    private boolean isNotInArray(int i) {
        return i < 0 || i >= orders.length;
    }
}