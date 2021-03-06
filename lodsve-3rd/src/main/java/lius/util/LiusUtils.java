
package lius.util;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.transform.Templates;

import lius.config.LiusField;
import lius.search.LiusHit;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;

/**
 *
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 *
 */

public class LiusUtils {

	static Logger logger = Logger.getRootLogger();


	public static void printResultsFormLiusHitsList(List ls) {

		System.out.println("Nb doc = " + ls.size());

		for (int i = 0; i < ls.size(); i++) {

			LiusHit lh = (LiusHit) ls.get(i);
			System.out.println("===========*****=============");
			System.out.println("Score = " + lh.getScore());
			System.out.println("Doc id = " + lh.getDocId());
			List liusHitFields = lh.getLiusFields();
			for (int j = 0; j < liusHitFields.size(); j++) {
				LiusField lf = (LiusField) liusHitFields.get(j);
				String name = lf.getLabel();
				String[] values = lf.getValues();
				System.out.print(name + " : ");
				for (int k = 0; k < values.length; k++) {
					System.out.println("\t" + values[k]);
				}
			}
			System.out.println("==============================");

		}

	}

	/**
	 *
	 * Permet d'enregistrer le Document XML JDOM dans un fichier.
	 *
	 * <br/><br/>
	 *
	 * Saves the XML JDOM Document in a file.
	 *
	 */

	public static void saveInXmlFile(Document doc, String file) {
		Format f = Format.getPrettyFormat().setEncoding("UTF-8");

		XMLOutputter xop = new XMLOutputter(f);

		try {

			xop.output(doc, new FileOutputStream(file));

		}

		catch (IOException ex) {

			logger.error(ex.getMessage());

		}

	}

	public static void XslTransformation(Document xmlDoc, String xsl,

										 PrintWriter out) {

		// response.setContentType("text/html");

		try {

			javax.xml.transform.TransformerFactory tFactory =

					javax.xml.transform.TransformerFactory.newInstance();

			javax.xml.transform.Source xmlSource = new JDOMSource(xmlDoc);

			Templates template = tFactory
					.newTemplates(new javax.xml.transform.stream.

							StreamSource(new

							FileInputStream(xsl)));

			javax.xml.transform.Transformer transformer = template
					.newTransformer();

			transformer.transform(xmlSource,

					new javax.xml.transform.stream.StreamResult(out));

		}

		catch (Exception e) {

			out.write(e.getMessage());

			logger.error(e.getMessage());

		}

		out.close();

	}

	/**
	 * Permet de parser un fichier XML et de retourner un objet de type JDOM
	 * Document. <br/><br/> Parse an XML file and returns a JDOM object.
	 */

	public static Document parse(InputStream is) {
		org.jdom.Document xmlDoc = new org.jdom.Document();
		try {
			SAXBuilder builder = new SAXBuilder();
			builder.setValidation(false);
			xmlDoc = builder.build(is);
		} catch (JDOMException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return xmlDoc;
	}

	public static List unzip(InputStream is) {
		List res = new ArrayList();
		try {
			ZipInputStream in = new ZipInputStream(is);
			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					stream.write(buf, 0, len);
				}
				InputStream isEntry = new ByteArrayInputStream(stream
						.toByteArray());
				File file = File.createTempFile("tmp", "_" + entry.getName());
				copyInputStream(isEntry, new BufferedOutputStream(
						new FileOutputStream(file)));
				res.add(file);
			}
			in.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return res;
	}

	private static void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

}