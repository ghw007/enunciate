/*
 * Copyright 2006-2008 Web Cohesion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.enunciate.samples.genealogy.services;

import com.sun.jersey.multipart.FormDataParam;
import org.codehaus.enunciate.Facet;
import org.codehaus.enunciate.contract.jaxrs.ResourceMethodSignature;
import org.codehaus.enunciate.jaxrs.*;
import org.codehaus.enunciate.samples.genealogy.data.Person;
import org.codehaus.enunciate.samples.genealogy.data.PersonExt;
import org.codehaus.enunciate.samples.genealogy.data.RootElementMapWrapper;
import org.codehaus.enunciate.samples.genealogy.exceptions.EisAccountException;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Collection;

/**
 * The person service is used to access persons in the database.
 * 
 * @author Ryan Heaton
 */
@WebService (
  targetNamespace = "http://enunciate.codehaus.org/samples/full"
)
public interface PersonService {

  /**
   * Stores a person in the database.
   *
   * @since Version E
   * @param person The person to store in the database.
   * @return The person that was stored (presumably modified for storage).
   * @HTTP 333 if something weird happens.
   */
  @SOAPBinding (
    parameterStyle = SOAPBinding.ParameterStyle.BARE
  )
  @PUT
  @Path ("/pedigree/person")
  @ResponseHeaders (
    @ResponseHeader( name = "Location", description = "The location of the person stored.")
  )
  Person storePerson(Person person);

  @GET
  @Path("/pedigree/personext/{id}")
  @StatusCodes ({
    @ResponseCode ( code = 404, condition = "The person is not found.")
  })
  @Warnings ({
    @ResponseCode ( code = 299, condition = "The reason the person wasn't found.")
  })
  PersonExt readExtPerson(@PathParam("id") String id);

  @GET
  @Path("/pedigree/admin/persons/{id}")
  @StatusCodes ({
    @ResponseCode ( code = 404, condition = "The person is not found.")
  })
  @Warnings ({
    @ResponseCode ( code = 299, condition = "The reason the person wasn't found.")
  })
  @Facet (name = "http://enunciate.codehaus.org/samples/full#admin" )
  PersonExt readPersonAdmin(@PathParam("id") String id);

  /**
   * Reads a set of persons from the database.  Intended as an example of
   * collections as SOAP parameters.
   * @param personIds The ids of the persons to read.
   * @return The persons that were read.
   * @throws ServiceException If the read of one or more of the people failed.
   */
  Collection<Person> readPersons(Collection<String> personIds) throws ServiceException, EisAccountException;

  /**
   * Deletes a person from the database.  Not a one-way method, but still void.
   *
   * @param PErsonId The id of the person.
   * @param message The message about the delete.
   * @throws ServiceException If some problem occurred when deleting the person.
   */
  @DELETE
  @Path("/remover/pedigree/person/{id}")
  void deletePerson(@PathParam ("id") String PErsonId, @HeaderParam("X-Message") String message) throws ServiceException;

  /**
   * Store some generic properties.
   *
   * @param map The map of generic properties.
   * @return The generic properties.
   * @throws ServiceException Upon a problem.
   */
  @PUT
  @Path("/properties/generic")
  RootElementMapWrapper storeGenericProperties(RootElementMapWrapper map) throws ServiceException;

  /**
   * Uploads some files.
   *
   * @param files The files
   * @param length The length(s) of the files.
   * @param somename Another name.
   */
  @WebMethod ( exclude = true )
  @POST
  @Path("/posterdude")
  @ResourceMethodSignature(
    input = Person.class,
    output = Person.class,
    queryParams = { @QueryParam("length"), @QueryParam("somename") }
  )
  void uploadFiles(DataHandler[] files, String length) throws ServiceException;

  @WebMethod (exclude = true)
  @POST
  @Path("/multipart")
  @Consumes( MediaType.MULTIPART_FORM_DATA )
  void postMultipart(@FormDataParam("file1") InputStream file1, @FormDataParam("file2") InputStream file2);
// todo: uncomment when wanting to spend time investigating why jaxb doesn't work with the JAX-WS types the same way it does its own.
//  /**
//   * Reads the family of a given person.  Tests out maps.
//   *
//   * @param personId The id of the person for which to read the family.
//   * @return The persons in the family by relationship type.
//   * @throws ServiceException If some problem occurred.
//   */
//  Map<RelationshipType, Person> readFamily(String personId) throws ServiceException;
}
