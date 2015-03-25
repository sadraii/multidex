/*
 * Copyright 2015, Mostafa Sadraii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.msadraii.multidex.util;

/**
 * Created by Mostafa on 3/19/2015.
 */
public class Utils {

    public static String toHexString(int color) {
     return "#" + Integer.toHexString(color);
    }

//    public static int safeLongToInt(long l) {
//        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
//            throw new IllegalArgumentException
//                    (l + " cannot be cast to int without changing its value.");
//        }
//        return (int) l;
//    }
}