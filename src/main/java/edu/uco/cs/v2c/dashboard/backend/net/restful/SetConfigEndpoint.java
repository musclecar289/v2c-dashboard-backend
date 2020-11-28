/*
 * Copyright (c) 2020 Caleb L. Power, Everistus Akpabio, Rashed Alrashed,
 * Nicholas Clemmons, Jonathan Craig, James Cole Riggall, and Glen Mathew.
 * All rights reserved. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uco.cs.v2c.dashboard.backend.net.restful;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uco.cs.v2c.dashboard.backend.V2CDashboardBackend;
import edu.uco.cs.v2c.dashboard.backend.net.APIVersion;
import edu.uco.cs.v2c.dashboard.backend.net.auth.AuthToken;
import spark.Request;
import spark.Response;

/**
 * Endpoint to handle config modifications.
 * 
 * @author Caleb L. Power
 */
public class SetConfigEndpoint extends Endpoint {
  
  /**
   * Instantiates the endpoint.
   */
  public SetConfigEndpoint() {
    super("/config", APIVersion.VERSION_1, HTTPMethod.PUT);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override public JSONObject doEndpointTask(Request req, Response res, AuthToken authToken) throws EndpointException {
    authorize(authToken, req, res); // require user to be logged in
    
    JSONObject globalConfig = null;
    JSONObject userConfig = null;
    
    try {
      JSONObject request = new JSONObject(req.body());
      if(request.has("global")) globalConfig = request.getJSONObject("global");
      if(request.has("user")) userConfig = request.getJSONObject("user");
      
      if(globalConfig != null)
        V2CDashboardBackend.getDatabase().setGlobalConfig(globalConfig);
      
      if(userConfig != null)
        V2CDashboardBackend.getDatabase().setUserConfig(
            authToken.getUser().getID(), userConfig);
      
      res.status(202);
      return new JSONObject()
          .put("status", "ok")
          .put("info", "Configuration updated.");
    } catch(JSONException e) {
      throw new EndpointException(req, "Syntax error: " + e.getMessage(), 400, e);
    }
  }

}
