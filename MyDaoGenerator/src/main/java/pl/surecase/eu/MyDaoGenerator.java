/*
 * Copyright 2015 Mostafa Sadraii
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

package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.sadraii.hyperdex");
        schema.enableKeepSectionsByDefault();

        Entity colorCode = schema.addEntity("ColorCode");
        colorCode.addIdProperty().unique();
        colorCode.addLongProperty("tag").unique();
        colorCode.addStringProperty("argb");
        colorCode.addStringProperty("task");

        Entity entry = schema.addEntity("Entry");
        entry.addIdProperty().unique();
        entry.addDateProperty("date");
        entry.addStringProperty("segments");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
