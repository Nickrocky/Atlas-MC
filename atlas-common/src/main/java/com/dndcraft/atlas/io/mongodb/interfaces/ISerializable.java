package com.dndcraft.atlas.io.mongodb.interfaces;

import org.bson.Document;

/**
 * @Author Nickrocky213
 * @Date 6/1/2021
 * For special occasions where you want absolute control over how a particular class is turned into a BSON document
 * */
public abstract interface ISerializable {

    Document toDocument();
    void fromDocument(Document document);

}
