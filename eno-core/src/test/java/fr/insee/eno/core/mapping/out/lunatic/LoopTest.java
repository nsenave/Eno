package fr.insee.eno.core.mapping.out.lunatic;

import fr.insee.eno.core.mappers.LunaticMapper;
import fr.insee.eno.core.model.navigation.Loop;
import fr.insee.eno.core.model.navigation.StandaloneLoop;
import fr.insee.lunatic.model.flat.ComponentTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Class to test Lunatic loop objects after mapping.
 * Loops are completely resolved in a processing class, so also look at tests of this processing. */
class LoopTest {

    private Loop enoLoop;
    private fr.insee.lunatic.model.flat.Loop lunaticLoop;

    @BeforeEach
    void loopObjects() {
        enoLoop = new StandaloneLoop();
        lunaticLoop = new fr.insee.lunatic.model.flat.Loop();
    }

    @Test
    void lunaticComponentType() {
        //
        LunaticMapper lunaticMapper = new LunaticMapper();
        lunaticMapper.mapEnoObject(enoLoop, lunaticLoop);
        //
        assertEquals(ComponentTypeEnum.LOOP, lunaticLoop.getComponentType());
    }

}
