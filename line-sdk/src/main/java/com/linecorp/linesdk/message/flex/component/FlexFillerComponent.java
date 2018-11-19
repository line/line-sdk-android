package com.linecorp.linesdk.message.flex.component;

/**
 * Represents an invisible component to fill extra space between components.
 * <li> The filler's flex property is fixed to 1.</li>
 * <li> The spacing property of the parent box will be ignored for fillers.</li>
 */
public class FlexFillerComponent extends FlexMessageComponent {
    public FlexFillerComponent() {
        super(Type.FILLER);
    }
}
