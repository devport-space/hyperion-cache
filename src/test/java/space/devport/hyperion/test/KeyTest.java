package space.devport.hyperion.test;

import org.junit.Assert;
import org.junit.Test;
import space.devport.hyperion.entry.Key;

public class KeyTest {

    @Test
    public void completeShouldComposeProperly() {
        Key key = new Key(
                "base",
                "collection",
                "identifier"
        );

        Assert.assertEquals("base.collection:identifier", key.compose());
    }

    @Test
    public void completeExtraDotShouldComposeProperly() {
        Key key = new Key(
                "longer.base",
                "collection",
                "identifier"
        );

        Assert.assertEquals("longer.base.collection:identifier", key.compose());
    }

    @Test
    public void withoutBaseShouldComposeProperly() {
        Key key = new Key(
                null,
                "collection",
                "identifier"
        );

        Assert.assertEquals("collection:identifier", key.compose());
    }

    @Test
    public void withoutCollectionShouldComposeProperly() {
        Key key = new Key(
                "base",
                null,
                "identifier"
        );

        Assert.assertEquals("base:identifier", key.compose());
    }

    @Test
    public void withoutBaseWithoutCollectionShouldComposeProperly() {
        Key key = new Key(
                null,
                null,
                "identifier"
        );

        Assert.assertEquals("identifier", key.compose());
    }

    @Test
    public void emptyShouldComposeProperly() {
        Key key = new Key(null, null, null);

        Assert.assertEquals("", key.compose());
    }
}
