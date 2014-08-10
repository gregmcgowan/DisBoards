package com.gregmcgowan.drownedinsound.data.network;

/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpCookie;

/**
 * A wrapper class around {@link org.apache.http.cookie.Cookie} and/or {@link org.apache.http.impl.cookie.BasicClientCookie}
 * designed for use in {@link com.loopj.android.http.PersistentCookieStore}.
 */
public class SerializableCookie implements Serializable {
    private static final long serialVersionUID = 6374381828722046732L;

    private transient HttpCookie cookie;

    public SerializableCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(cookie.getName());
        out.writeObject(cookie.getValue());
        out.writeObject(cookie.getComment());
        out.writeObject(cookie.getDomain());
        out.writeObject(cookie.getPath());
        out.writeInt(cookie.getVersion());
        out.writeBoolean(cookie.getSecure());
        out.writeLong(cookie.getMaxAge());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        String name = (String)in.readObject();
        String value = (String)in.readObject();
        cookie = new HttpCookie(name,value);
        cookie.setComment((String)in.readObject());
        cookie.setDomain((String)in.readObject());
        cookie.setPath((String)in.readObject());
        cookie.setVersion(in.readInt());
        cookie.setSecure(in.readBoolean());
        cookie.setMaxAge(in.readLong());
    }
}