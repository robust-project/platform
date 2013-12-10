/** 
*Copyright 2013 Software Mind SA
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
package pl.softwaremind.robust.healthindicatorservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


@WebService(targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService", name = "HealthIndicatorServiceSoap")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface HealthIndicatorServiceSoap {

    @WebResult(name = "getIndicatorsResponse", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService", partName = "body")
    @WebMethod
    public GetIndicatorsResponse getIndicators(
        @WebParam(partName = "body", name = "getIndicatorsRequest", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService")
        GetIndicatorsRequest body
    );

    @WebResult(name = "getHealthScoreResponse", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService", partName = "body")
    @WebMethod
    public GetHealthScoreResponse getHealthScore(
        @WebParam(partName = "body", name = "getHealthScoreRequest", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService")
        GetHealthScoreRequest body
    );

    @WebResult(name = "getCommunitiesResponse", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService", partName = "body")
    @WebMethod
    public GetCommunitiesResponse getCommunities(
        @WebParam(partName = "body", name = "getCommunitiesRequest", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService")
        GetCommunitiesRequest body
    );

    @WebResult(name = "getHealthScoresResponse", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService", partName = "body")
    @WebMethod
    public GetHealthScoresResponse getHealthScores(
        @WebParam(partName = "body", name = "getHealthScoresRequest", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService")
        GetHealthScoresRequest body
    );

    @WebResult(name = "getDataSourcesResponse", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService", partName = "body")
    @WebMethod
    public GetDataSourcesResponse getDataSources(
        @WebParam(partName = "body", name = "getDataSourcesRequest", targetNamespace = "http://robust.softwaremind.pl/HealthIndicatorService")
        GetDataSourcesRequest body
    );
}
