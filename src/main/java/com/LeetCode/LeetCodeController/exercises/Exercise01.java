package com.LeetCode.LeetCodeController.exercises;

public class Exercise01 {

    public static void main(String[] args) {
        int[] a = {5, 7, 6, 9, 8, 1, -15, 2, 6};
        int sum = 0;

        System.out.print("Array: ");
        for (int num : a) {
            System.out.print(num + " ");
            sum += num;
        }

        System.out.println("\nSum: " + sum);
    }
}