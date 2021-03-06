package loops.sn00p.api.config.yaml;

import loops.sn00p.api.config.Config;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;

/**
 *
 * Abstract parent class to all YAML configuration files.
 * Allows all child files to be serializable and when initialised using the {@link YamlConfigFactory} will auto-fill
 * the values from the object to the config file.
 *
 */
@ConfigSerializable
public abstract class AbstractYamlConfig implements Config {

    protected transient ConfigurationReference<CommentedConfigurationNode> base;
    protected transient ValueReference<?, CommentedConfigurationNode> config;

    protected AbstractYamlConfig() {}
    
    @Override
    public ConfigurationNode getNode() {
        return this.config.node();
    }

    @Override
    public void save() {
        try {
            this.base.save();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }
}
