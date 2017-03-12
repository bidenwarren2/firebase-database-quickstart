/**
 * Copyright Google Inc. All Rights Reserved.
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
package com.google.firebase.quickstart.email;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.sendgrid.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to send email notifications from the server.
 */
public class MyEmailer {

    public static void sendNotificationEmail(String email, String uid, String postId) {
        System.out.println("sendNotificationEmail: " + email);

        try {
            SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
            Request request = new Request();
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = "{\"personalizations\":" +
                "[{\"to\":[{\"email\":\"" + email + "\"}]," +
                "\"subject\":\"New Post!\"}]," +
                "\"from\":{\"email\":\"test@example.com\"}," +
                "\"content\":[{\"type\":\"text/plain\",\"value\": \"There was new post!\"}]}";
            Response response = sg.api(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);


            // Save the date of the last notification sent
            Map<String, Object> update = new HashMap<String, Object>();
            update.put("/posts/" + postId + "/lastNotificationTimestamp", ServerValue.TIMESTAMP);
            update.put("/user-posts/" + uid + "/" + postId + "/lastNotificationTimestamp", ServerValue.TIMESTAMP);

            FirebaseDatabase.getInstance().getReference().updateChildren(update);
        } catch (IOException ex) {
            System.out.println("Whoops! Unable to send email.");
            ex.printStackTrace();
        }
    }

}