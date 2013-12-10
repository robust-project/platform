/**
 * Copyright 2013 DERI, National University of Ireland Galway.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package deri.uimr.ufstreamfeeder;

import deri.uimr.utilities.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.AsyncEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns={"/"})
public class ControlServlet extends HttpServlet {
    public static double MIN_INTERVAL_MILLIS = 10000.0;

    private static final MySQLManager mySQLManager = MySQLManager.getInstance();
    private final FeedingRunnable feedingRunnable;

    public ControlServlet(final FeedingRunnable feedingRunnable) {
        this.feedingRunnable = feedingRunnable;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // Check parameters
        final String action = request.getParameter("action");
        if( action == null ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'action' parameter");
            return;
        }

        // Process actions
        if( action.equalsIgnoreCase("reset") ) { // Reset (truncate) the entire database
            if( mySQLManager.truncateAll() ) {
                sendTextResponse(response, "Database successfully truncated.");
            } else sendTextResponse(response, "Sorry, something went wrong.");
        } else if( action.equalsIgnoreCase("set_commit_interval") ) { // Set a new commit interval
            final String intervalMillisStr = request.getParameter("interval_millis");
            if( intervalMillisStr == null ) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing 'interval_millis' parameter for 'set_commit_interval' action");
                return;
            }
            try {
                final double intervalMillis = Double.parseDouble(intervalMillisStr);
                if( intervalMillis < MIN_INTERVAL_MILLIS ) {
                    response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                        String.format("'interval_millis' value must be greater or equal than %f", MIN_INTERVAL_MILLIS));
                    return;
                }
                feedingRunnable.setCommitIntervalMillis(intervalMillis);
                sendTextResponse(response, String.format("Commit interval successfully set to %f milliseconds.", intervalMillis));
            }
            catch( NumberFormatException e ) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'interval_millis' must be a floating point number");
                return;
            }
        } else if( action.equalsIgnoreCase("commit") ) { // Force a commit to the database
            feedingRunnable.skipCurrentSuspend();
            sendTextResponse(response, "Current queue successfully committed.");
        } else { // Unknown action
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "Unknown action given");
        }
    }

    private void sendTextResponse(final HttpServletResponse response, final String text) throws IOException {
       response.setContentType("text/plain; charset=utf-8");
       response.setHeader("Cache-Control", "no-cache, must-revalidate, post-check=0, pre-check=0");
       response.setHeader("Pragma", "no-cache");
       response.setHeader("Expires", "0");
       response.setStatus(HttpServletResponse.SC_OK);
       response.getWriter().println(text);
       Logger.log(text);
    }
}
