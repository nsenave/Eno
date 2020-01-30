package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.generation.DDI2FRGenerator;
import fr.insee.eno.postprocessing.fr.FRBrowsingPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;
import fr.insee.eno.service.GenerationService;

public class TestDDIToXForm {

	private DDIPreprocessor ddiPreprocessor = new DDIPreprocessor();
	
	private DDI2FRGenerator ddi2fr = new DDI2FRGenerator();
	
	private FRBrowsingPostprocessor frBrowsing = new FRBrowsingPostprocessor();
	
	private XMLDiff xmlDiff = new XMLDiff();

	@Test
	public void simpleDiffTest() {
		try {
			String basePath = "src/test/resources/ddi-to-xform";
			GenerationService genService = new GenerationService(ddiPreprocessor,ddi2fr,frBrowsing);
			File in = new File(String.format("%s/in.xml", basePath));
			File outputFile = genService.generateQuestionnaire(in, "ddi-2-fr-test");
			File expectedFile = new File(String.format("%s/out.xhtml", basePath));
			Diff diff = xmlDiff.getDiff(outputFile, expectedFile);
			Assert.assertFalse(getDiffMessage(diff, basePath), diff.hasDifferences());

		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assert.fail();
		}
	}

	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}
}
