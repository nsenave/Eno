package fr.insee.eno.transform.xsl;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main Saxon Service used to perform XSLT transformations
 * 
 * @author gerose
 *
 */
public class XslTransformation {

	final static Logger logger = LogManager.getLogger(XslTransformation.class);

	/**
	 * Main Saxon transformation method
	 * 
	 * @param transformer
	 *            : The defined transformer with his embedded parameters
	 *            (defined in the other methods of this class)
	 * @param xmlInput
	 *            : The input xml file where the XSLT will be applied
	 * @param xmlOutput
	 *            : The output xml file after the transformation
	 * @throws Exception
	 *             : Mainly if the input/output files path are incorrect
	 */
	public void xslTransform(Transformer transformer, File xmlInput, File xmlOutput) throws Exception {
		logger.debug("Starting xsl transformation -Input : " + xmlInput + " -Output : " + xmlOutput);
		transformer.transform(new StreamSource(xmlInput), new StreamResult(xmlOutput));
	}

	/**
	 * Basic Transformer initialization without parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transform(File input, File xslSheet, File output) throws Exception {
		logger.debug("Using the basic transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		xslTransform(transformer, input, output);
	}

	/**
	 * Incorporation Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param generatedFileParameter
	 *            : Incorporation xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformIncorporation(File input, File xslSheet, File output, File generatedFileParameter)
			throws Exception {
		logger.debug("Using the incorporation transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setParameter(XslParameters.INCORPORATION_GENERATED_FILE, generatedFileParameter.toURI().toString());
		xslTransform(transformer, input, output);
	}

	/**
	 * Dereferencing Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param outputFolderParameter
	 *            : Dereferencing xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformDereferencing(File input, File xslSheet, File output, File outputFolderParameter)
			throws Exception {
		logger.debug("Using the dereferencing transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setParameter(XslParameters.DEREFERENCING_OUTPUT_FOLDER, outputFolderParameter);
		xslTransform(transformer, input, output);
	}

	/**
	 * Titling Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param parametersFileParameter
	 *            : Titling xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformTitling(File input, File xslSheet, File output, File parametersFileParameter)
			throws Exception {
		logger.debug("Using the titling transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setParameter(XslParameters.TITLING_PARAMETERS_FILE, parametersFileParameter.toURI().toString());
		xslTransform(transformer, input, output);
	}

	/**
	 * Ddi2fr Transformer initialization with its parameters
	 * 
	 * @param input
	 *            : the input xml file
	 * @param xslSheet
	 *            : the xsl stylesheet that will be used
	 * @param output
	 *            : the xml output that will be created
	 * @param campaignParameter
	 *            : Ddi2fr xsl parameter
	 * @param modelParameter
	 *            : Ddi2fr xsl parameter
	 * @param propertiesFileParameter
	 *            : Ddi2fr xsl parameter
	 * @throws Exception
	 *             : if the factory couldn't be found or if the paths are
	 *             incorrect
	 */
	public void transformDdi2frBasicForm(File input, File xslSheet, File output, String campaignParameter,
			File modelParameter, File propertiesFileParameter) throws Exception {
		logger.debug("Using the DDI to XForms transformer");
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

		Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
		transformer.setParameter(XslParameters.DDI2FR_CAMPAIGN, campaignParameter);
		transformer.setParameter(XslParameters.DDI2FR_MODEL, modelParameter);
		transformer.setParameter(XslParameters.DDI2FR_PROPERTIES_FILE, propertiesFileParameter.toURI().toString());
		xslTransform(transformer, input, output);
	}

}
