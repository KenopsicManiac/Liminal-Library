package org.dimdev.limlib.client.specialmodels.iris;

import java.util.Arrays;
import java.util.Objects;

public record SpecialModelOutputContract(
    int fragmentOutputCount,
    int[] defaultDrawBuffers,
    boolean writesDepth,
    boolean requiresPreTranslucentDepth,
    boolean requiresMainDepth,
    SpecialModelBlendMode blendMode
) {

    public SpecialModelOutputContract {
        if (fragmentOutputCount < 1) {
            throw new IllegalArgumentException("fragmentOutputCount must be at least 1");
        }

        Objects.requireNonNull(defaultDrawBuffers, "defaultDrawBuffers");
        Objects.requireNonNull(blendMode, "blendMode");
        defaultDrawBuffers = defaultDrawBuffers.clone();

        if (defaultDrawBuffers.length < fragmentOutputCount) {
            throw new IllegalArgumentException("defaultDrawBuffers must contain at least one buffer per fragment output");
        }
    }

    @Override
    public int[] defaultDrawBuffers() {
        return this.defaultDrawBuffers.clone();
    }

    public static SpecialModelOutputContract singleColor() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int fragmentOutputCount = 1;
        private int[] defaultDrawBuffers = new int[] {0};
        private boolean writesDepth;
        private boolean requiresPreTranslucentDepth;
        private boolean requiresMainDepth;
        private SpecialModelBlendMode blendMode = SpecialModelBlendMode.NONE;

        public Builder fragmentOutputCount(int fragmentOutputCount) {
            this.fragmentOutputCount = fragmentOutputCount;
            return this;
        }

        public Builder defaultDrawBuffers(int... defaultDrawBuffers) {
            this.defaultDrawBuffers = defaultDrawBuffers.clone();
            return this;
        }

        public Builder writesDepth(boolean writesDepth) {
            this.writesDepth = writesDepth;
            return this;
        }

        public Builder requiresPreTranslucentDepth(boolean requiresPreTranslucentDepth) {
            this.requiresPreTranslucentDepth = requiresPreTranslucentDepth;
            return this;
        }

        public Builder requiresMainDepth(boolean requiresMainDepth) {
            this.requiresMainDepth = requiresMainDepth;
            return this;
        }

        public Builder blendMode(SpecialModelBlendMode blendMode) {
            this.blendMode = Objects.requireNonNull(blendMode, "blendMode");
            return this;
        }

        public SpecialModelOutputContract build() {
            int[] drawBuffers = this.defaultDrawBuffers;

            if (drawBuffers.length < this.fragmentOutputCount) {
                drawBuffers = Arrays.copyOf(drawBuffers, this.fragmentOutputCount);

                for (int i = this.defaultDrawBuffers.length; i < drawBuffers.length; i++) {
                    drawBuffers[i] = i;
                }
            }

            return new SpecialModelOutputContract(
                this.fragmentOutputCount,
                drawBuffers,
                this.writesDepth,
                this.requiresPreTranslucentDepth,
                this.requiresMainDepth,
                this.blendMode);
        }
    }
}
