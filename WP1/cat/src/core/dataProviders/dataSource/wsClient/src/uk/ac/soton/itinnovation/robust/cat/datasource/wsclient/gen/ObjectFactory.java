/////////////////////////////////////////////////////////////////////////
//
// © University of Southampton IT Innovation Centre, 2012
//
// Copyright in this software belongs to University of Southampton
// IT Innovation Centre of Gamma House, Enterprise Road, 
// Chilworth Science Park, Southampton, SO16 7NS, UK.
//
// This software may not be used, sold, licensed, transferred, copied
// or reproduced in whole or in part in any manner or form or in or
// on any media by any person other than in accordance with the terms
// of the Licence Agreement supplied with the software, or otherwise
// without the prior written consent of the copyright owners.
//
// This software is distributed WITHOUT ANY WARRANTY, without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE, except where stated in the Licence Agreement supplied with
// the software.
//
//      Created By :            Ken Meachem
//      Created Date :          2013-7-13
//      Created for Project :   ROBUST
//
/////////////////////////////////////////////////////////////////////////
package uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetAllUserAccountsResponse_QNAME = new QName("http://ws.robust.swmind.pl/", "getAllUserAccountsResponse");
    private final static QName _GetAllPostsResponse_QNAME = new QName("http://ws.robust.swmind.pl/", "getAllPostsResponse");
    private final static QName _GetUserAccountResponse_QNAME = new QName("http://ws.robust.swmind.pl/", "getUserAccountResponse");
    private final static QName _GetPostsByForumUriResponse_QNAME = new QName("http://ws.robust.swmind.pl/", "getPostsByForumUriResponse");
    private final static QName _GetPostsResponse_QNAME = new QName("http://ws.robust.swmind.pl/", "getPostsResponse");
    private final static QName _GetPostsByForumUri_QNAME = new QName("http://ws.robust.swmind.pl/", "getPostsByForumUri");
    private final static QName _GetPostsByUserUriResponse_QNAME = new QName("http://ws.robust.swmind.pl/", "getPostsByUserUriResponse");
    private final static QName _GetAllPosts_QNAME = new QName("http://ws.robust.swmind.pl/", "getAllPosts");
    private final static QName _GetPostsByUserUri_QNAME = new QName("http://ws.robust.swmind.pl/", "getPostsByUserUri");
    private final static QName _GetPostsByUserName_QNAME = new QName("http://ws.robust.swmind.pl/", "getPostsByUserName");
    private final static QName _GetPostsByUserNameResponse_QNAME = new QName("http://ws.robust.swmind.pl/", "getPostsByUserNameResponse");
    private final static QName _GetAllForums_QNAME = new QName("http://ws.robust.swmind.pl/", "getAllForums");
    private final static QName _GetAllUserAccounts_QNAME = new QName("http://ws.robust.swmind.pl/", "getAllUserAccounts");
    private final static QName _GetUserAccount_QNAME = new QName("http://ws.robust.swmind.pl/", "getUserAccount");
    private final static QName _GetPosts_QNAME = new QName("http://ws.robust.swmind.pl/", "getPosts");
    private final static QName _GetAllForumsResponse_QNAME = new QName("http://ws.robust.swmind.pl/", "getAllForumsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.ac.soton.itinnovation.robust.cat.datasource.wsclient.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetPosts }
     * 
     */
    public GetPosts createGetPosts() {
        return new GetPosts();
    }

    /**
     * Create an instance of {@link GetUserAccount }
     * 
     */
    public GetUserAccount createGetUserAccount() {
        return new GetUserAccount();
    }

    /**
     * Create an instance of {@link GetPostsByUserUriResponse }
     * 
     */
    public GetPostsByUserUriResponse createGetPostsByUserUriResponse() {
        return new GetPostsByUserUriResponse();
    }

    /**
     * Create an instance of {@link GetAllUserAccounts }
     * 
     */
    public GetAllUserAccounts createGetAllUserAccounts() {
        return new GetAllUserAccounts();
    }

    /**
     * Create an instance of {@link GetAllPosts }
     * 
     */
    public GetAllPosts createGetAllPosts() {
        return new GetAllPosts();
    }

    /**
     * Create an instance of {@link GetPostsByUserName }
     * 
     */
    public GetPostsByUserName createGetPostsByUserName() {
        return new GetPostsByUserName();
    }

    /**
     * Create an instance of {@link UserDto }
     * 
     */
    public UserDto createUserDto() {
        return new UserDto();
    }

    /**
     * Create an instance of {@link GetPostsResponse }
     * 
     */
    public GetPostsResponse createGetPostsResponse() {
        return new GetPostsResponse();
    }

    /**
     * Create an instance of {@link GetAllUserAccountsResponse }
     * 
     */
    public GetAllUserAccountsResponse createGetAllUserAccountsResponse() {
        return new GetAllUserAccountsResponse();
    }

    /**
     * Create an instance of {@link PostDto }
     * 
     */
    public PostDto createPostDto() {
        return new PostDto();
    }

    /**
     * Create an instance of {@link GetAllForumsResponse }
     * 
     */
    public GetAllForumsResponse createGetAllForumsResponse() {
        return new GetAllForumsResponse();
    }

    /**
     * Create an instance of {@link GetPostsByUserUri }
     * 
     */
    public GetPostsByUserUri createGetPostsByUserUri() {
        return new GetPostsByUserUri();
    }

    /**
     * Create an instance of {@link GetPostsByUserNameResponse }
     * 
     */
    public GetPostsByUserNameResponse createGetPostsByUserNameResponse() {
        return new GetPostsByUserNameResponse();
    }

    /**
     * Create an instance of {@link GetAllForums }
     * 
     */
    public GetAllForums createGetAllForums() {
        return new GetAllForums();
    }

    /**
     * Create an instance of {@link GetPostsByForumUri }
     * 
     */
    public GetPostsByForumUri createGetPostsByForumUri() {
        return new GetPostsByForumUri();
    }

    /**
     * Create an instance of {@link GetUserAccountResponse }
     * 
     */
    public GetUserAccountResponse createGetUserAccountResponse() {
        return new GetUserAccountResponse();
    }

    /**
     * Create an instance of {@link ForumDto }
     * 
     */
    public ForumDto createForumDto() {
        return new ForumDto();
    }

    /**
     * Create an instance of {@link GetAllPostsResponse }
     * 
     */
    public GetAllPostsResponse createGetAllPostsResponse() {
        return new GetAllPostsResponse();
    }

    /**
     * Create an instance of {@link GetPostsByForumUriResponse }
     * 
     */
    public GetPostsByForumUriResponse createGetPostsByForumUriResponse() {
        return new GetPostsByForumUriResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllUserAccountsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getAllUserAccountsResponse")
    public JAXBElement<GetAllUserAccountsResponse> createGetAllUserAccountsResponse(GetAllUserAccountsResponse value) {
        return new JAXBElement<GetAllUserAccountsResponse>(_GetAllUserAccountsResponse_QNAME, GetAllUserAccountsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPostsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getAllPostsResponse")
    public JAXBElement<GetAllPostsResponse> createGetAllPostsResponse(GetAllPostsResponse value) {
        return new JAXBElement<GetAllPostsResponse>(_GetAllPostsResponse_QNAME, GetAllPostsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserAccountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getUserAccountResponse")
    public JAXBElement<GetUserAccountResponse> createGetUserAccountResponse(GetUserAccountResponse value) {
        return new JAXBElement<GetUserAccountResponse>(_GetUserAccountResponse_QNAME, GetUserAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPostsByForumUriResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getPostsByForumUriResponse")
    public JAXBElement<GetPostsByForumUriResponse> createGetPostsByForumUriResponse(GetPostsByForumUriResponse value) {
        return new JAXBElement<GetPostsByForumUriResponse>(_GetPostsByForumUriResponse_QNAME, GetPostsByForumUriResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPostsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getPostsResponse")
    public JAXBElement<GetPostsResponse> createGetPostsResponse(GetPostsResponse value) {
        return new JAXBElement<GetPostsResponse>(_GetPostsResponse_QNAME, GetPostsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPostsByForumUri }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getPostsByForumUri")
    public JAXBElement<GetPostsByForumUri> createGetPostsByForumUri(GetPostsByForumUri value) {
        return new JAXBElement<GetPostsByForumUri>(_GetPostsByForumUri_QNAME, GetPostsByForumUri.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPostsByUserUriResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getPostsByUserUriResponse")
    public JAXBElement<GetPostsByUserUriResponse> createGetPostsByUserUriResponse(GetPostsByUserUriResponse value) {
        return new JAXBElement<GetPostsByUserUriResponse>(_GetPostsByUserUriResponse_QNAME, GetPostsByUserUriResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllPosts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getAllPosts")
    public JAXBElement<GetAllPosts> createGetAllPosts(GetAllPosts value) {
        return new JAXBElement<GetAllPosts>(_GetAllPosts_QNAME, GetAllPosts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPostsByUserUri }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getPostsByUserUri")
    public JAXBElement<GetPostsByUserUri> createGetPostsByUserUri(GetPostsByUserUri value) {
        return new JAXBElement<GetPostsByUserUri>(_GetPostsByUserUri_QNAME, GetPostsByUserUri.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPostsByUserName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getPostsByUserName")
    public JAXBElement<GetPostsByUserName> createGetPostsByUserName(GetPostsByUserName value) {
        return new JAXBElement<GetPostsByUserName>(_GetPostsByUserName_QNAME, GetPostsByUserName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPostsByUserNameResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getPostsByUserNameResponse")
    public JAXBElement<GetPostsByUserNameResponse> createGetPostsByUserNameResponse(GetPostsByUserNameResponse value) {
        return new JAXBElement<GetPostsByUserNameResponse>(_GetPostsByUserNameResponse_QNAME, GetPostsByUserNameResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllForums }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getAllForums")
    public JAXBElement<GetAllForums> createGetAllForums(GetAllForums value) {
        return new JAXBElement<GetAllForums>(_GetAllForums_QNAME, GetAllForums.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllUserAccounts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getAllUserAccounts")
    public JAXBElement<GetAllUserAccounts> createGetAllUserAccounts(GetAllUserAccounts value) {
        return new JAXBElement<GetAllUserAccounts>(_GetAllUserAccounts_QNAME, GetAllUserAccounts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserAccount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getUserAccount")
    public JAXBElement<GetUserAccount> createGetUserAccount(GetUserAccount value) {
        return new JAXBElement<GetUserAccount>(_GetUserAccount_QNAME, GetUserAccount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPosts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getPosts")
    public JAXBElement<GetPosts> createGetPosts(GetPosts value) {
        return new JAXBElement<GetPosts>(_GetPosts_QNAME, GetPosts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllForumsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.robust.swmind.pl/", name = "getAllForumsResponse")
    public JAXBElement<GetAllForumsResponse> createGetAllForumsResponse(GetAllForumsResponse value) {
        return new JAXBElement<GetAllForumsResponse>(_GetAllForumsResponse_QNAME, GetAllForumsResponse.class, null, value);
    }

}
