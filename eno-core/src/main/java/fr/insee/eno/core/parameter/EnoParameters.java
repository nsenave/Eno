package fr.insee.eno.core.parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.eno.core.model.mode.Mode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class EnoParameters {

    public enum Context {HOUSEHOLD, BUSINESS, DEFAULT}
    public enum ModeParameter {CAPI, CATI, CAWI, PAPI, PROCESS}
    public enum Language {FR, EN, IT, ES, DE}
    public enum QuestionNumberingMode {NONE, SEQUENCE, ALL}
    public enum LunaticPaginationMode {NONE, SEQUENCE, QUESTION}

    public static final String DEFAULT_CAMPAIGN_NAME = "test-2020-x00";

    // Context parameters
    private Context context;
    private String campaignName = DEFAULT_CAMPAIGN_NAME; // unused yet
    private ModeParameter modeParameter;
    private Language language = Language.FR; // unused yet

    // Eno core parameters
    private boolean identificationQuestion;
    private boolean responseTimeQuestion;
    private boolean commentSection;
    private boolean sequenceNumbering;
    private QuestionNumberingMode questionNumberingMode;
    private boolean arrowCharInQuestions;
    private List<Mode> selectedModes = new ArrayList<>();

    // Lunatic parameters
    private boolean controls;
    private boolean toolTip; // Not implemented yet in Lunatic
    private boolean missingVariables;
    private boolean filterResult;
    private boolean filterDescription;
    private LunaticPaginationMode lunaticPaginationMode;

    private EnoParameters() {}

    public static EnoParameters emptyValues() {
        return new EnoParameters();
    }

    public static EnoParameters of(Context context, Format outFormat, ModeParameter modeParameter) {
        log.info("Parameters with context {}, out format {}, mode {}", context, outFormat, modeParameter);
        EnoParameters enoParameters = new EnoParameters();
        //
        if (! Format.LUNATIC.equals(outFormat))
            throw new UnsupportedOperationException("Only Lunatic out format is supported for now.");
        //
        enoParameters.setContext(context);
        enoParameters.setModeParameter(modeParameter);
        //
        switch (modeParameter) {
            case CAPI -> enoParameters.setSelectedModes(List.of(Mode.CAPI));
            case CATI -> enoParameters.setSelectedModes(List.of(Mode.CATI));
            case CAWI -> enoParameters.setSelectedModes(List.of(Mode.CAWI));
            case PAPI -> throw new IllegalArgumentException("Mode 'PAPI' is not compatible with Lunatic format.");
            case PROCESS -> enoParameters.setSelectedModes(List.of(Mode.CAPI, Mode.CATI, Mode.CAWI));
        }
        //
        enoParameters.setIdentificationQuestion(Context.BUSINESS.equals(context));
        enoParameters.setResponseTimeQuestion(Context.BUSINESS.equals(context));
        enoParameters.setCommentSection(Context.HOUSEHOLD.equals(context) || Context.BUSINESS.equals(context));
        enoParameters.setQuestionNumberingMode(
                Context.HOUSEHOLD.equals(context) ? QuestionNumberingMode.ALL : QuestionNumberingMode.SEQUENCE);
        enoParameters.sequenceNumbering = true;
        enoParameters.arrowCharInQuestions = true;
        //
        boolean isInterview = ModeParameter.CAPI.equals(modeParameter) || ModeParameter.CATI.equals(modeParameter);
        boolean isWeb = ModeParameter.CAWI.equals(modeParameter);
        boolean isProcess = ModeParameter.PROCESS.equals(modeParameter);
        enoParameters.setControls(isWeb || isProcess);
        enoParameters.setToolTip(isWeb || isProcess);
        enoParameters.setFilterDescription(isProcess);
        enoParameters.setFilterResult(isWeb);
        enoParameters.setMissingVariables(isInterview);
        enoParameters.setLunaticPaginationMode(
                isInterview || isWeb ? LunaticPaginationMode.QUESTION : LunaticPaginationMode.NONE);
        //
        return enoParameters;
    }

    public static EnoParameters parse(InputStream parametersInputStream) throws IOException {
        log.info("Parsing Eno parameters from input stream");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(parametersInputStream, EnoParameters.class);
    }

    public static String serialize(EnoParameters enoParameters) throws JsonProcessingException {
        log.info("Serializing parameters file");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(enoParameters);
    }

}
