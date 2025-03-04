package Util;

import Models.ParticleSystem;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Texture;

public class ParticleSystemRenderer {
    private Texture texParticle;

    public void initialize(String filenameTexture) {
        texParticle = new Texture(filenameTexture);
    }

    public void cleanup() {
        if (texParticle != null) {
            texParticle.cleanup();
        }
    }

    public void render(Graphics2D graphics, ParticleSystem system) {
        for (var particle : system.getParticles()) {
            graphics.draw(texParticle, particle.area, particle.rotation, particle.center, Color.WHITE);
        }
    }

}
