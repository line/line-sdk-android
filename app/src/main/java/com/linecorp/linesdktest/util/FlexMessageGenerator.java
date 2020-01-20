package com.linecorp.linesdktest.util;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.message.FlexMessage;
import com.linecorp.linesdk.message.flex.action.UriAction;
import com.linecorp.linesdk.message.flex.component.FlexBoxComponent;
import com.linecorp.linesdk.message.flex.component.FlexButtonComponent;
import com.linecorp.linesdk.message.flex.component.FlexImageComponent;
import com.linecorp.linesdk.message.flex.component.FlexMessageComponent;
import com.linecorp.linesdk.message.flex.component.FlexSeparatorComponent;
import com.linecorp.linesdk.message.flex.component.FlexTextComponent;
import com.linecorp.linesdk.message.flex.container.FlexBubbleContainer;
import com.linecorp.linesdk.message.flex.container.FlexCarouselContainer;
import com.linecorp.linesdk.message.flex.container.FlexMessageContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlexMessageGenerator {

    @NonNull
    public FlexMessage createFlexMessageWithTextComponent() {
        FlexBoxComponent boxComponent = FlexBoxComponent.newBuilder(
                FlexMessageComponent.Layout.HORIZONTAL,
                Arrays.asList(
                        FlexTextComponent.newBuilder("Hello, ").build(),
                        FlexTextComponent.newBuilder("World!").build()))
                .build();
        FlexMessageContainer flexMessageContainer = FlexBubbleContainer.newBuilder()
                .setBody(boxComponent).build();
        return new FlexMessage("This is a Hello World Flex Message", flexMessageContainer);
    }

    @NonNull
    public FlexMessage createFlexBubbleContainerMessage() {
        return new FlexMessage("This is a Bubble Container Flex Message",
                createBubbleContainer());
    }

    @NonNull
    public FlexMessage createFlexCarouselContainerMessage() {
        return new FlexMessage("This is a Carousel Container Flex Message",
                new FlexCarouselContainer(createBubbleContainerList()));
    }

    /**
     * This function help to generate 10 duplicated Bubble Containers and return as a List:
     *
     * @return the generated list of 10 Bubble Containers
     */
    @NonNull
    private List<FlexBubbleContainer> createBubbleContainerList() {
        FlexBubbleContainer bubbleContainer = createBubbleContainer();
        List<FlexBubbleContainer> bubbleContainerList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            bubbleContainerList.add(bubbleContainer);
        }
        return bubbleContainerList;
    }

    @NonNull
    private FlexBubbleContainer createBubbleContainer() {
        return FlexBubbleContainer.newBuilder()
                .setHeader(createBoxComponentForHeaderField())
                .setHero(createImageComponentForHeroField())
                .setBody(createBoxComponentForBodyField())
                .setFooter(createBoxComponentForFooterField())
                .build();
    }

    @NonNull
    private FlexBoxComponent createBoxComponentForFooterField() {
        return FlexBoxComponent.newBuilder(FlexMessageComponent.Layout.HORIZONTAL,
                Arrays.asList(FlexButtonComponent.newBuilder(
                        new UriAction("https://linecorp.com", "More"))
                        .build()
                ))
                .setSpacing(FlexMessageComponent.Margin.MD)
                .build();
    }

    @NonNull
    private FlexBoxComponent createBoxComponentForBodyField() {
        return FlexBoxComponent.newBuilder(FlexMessageComponent.Layout.HORIZONTAL,
                Arrays.asList(createBodyContentLeftBoxComponent(),
                        createBodyContentRightBoxComponent()))
                .setSpacing(FlexMessageComponent.Margin.MD)
                .build();
    }

    /**
     * This function help to generate 2 images like below:
     * <p>
     * #image 1#
     * #image 2#
     *
     * @return the generated FlexBoxComponent with 2 vertically arranged images.
     */
    @NonNull
    private FlexBoxComponent createBodyContentLeftBoxComponent() {
        FlexImageComponent thumbnail1 = FlexImageComponent
                .newBuilder("https://scdn.line-apps.com/n/channel_devcenter/img/fx/02_1_news_thumbnail_1.png")
                .setSize(FlexMessageComponent.Size.SM)
                .setAspectRatio(FlexMessageComponent.AspectRatio.RATIO_4x3)
                .setAspectMode(FlexMessageComponent.AspectMode.COVER)
                .setGravity(FlexMessageComponent.Gravity.BOTTOM).build();
        FlexImageComponent thumbnail2 = FlexImageComponent
                .newBuilder("https://scdn.line-apps.com/n/channel_devcenter/img/fx/02_1_news_thumbnail_2.png")
                .setSize(FlexMessageComponent.Size.SM)
                .setAspectRatio(FlexMessageComponent.AspectRatio.RATIO_4x3)
                .setAspectMode(FlexMessageComponent.AspectMode.COVER)
                .setMargin(FlexMessageComponent.Margin.MD).build();

        return FlexBoxComponent.newBuilder(FlexMessageComponent.Layout.VERTICAL,
                Arrays.asList(thumbnail1, thumbnail2))
                .setFlex(1)
                .build();
    }

    /**
     * This function help to generate 4 line of texts with 3 separators like below:
     * <p>
     * text1
     * ------
     * text2
     * ------
     * text3
     * ------
     * text4
     *
     * @return the generated FlexBoxComponent with 4 vertically arranged texts.
     */
    @NonNull
    private FlexBoxComponent createBodyContentRightBoxComponent() {
        FlexSeparatorComponent separatorComponent = new FlexSeparatorComponent();
        FlexTextComponent text1 = FlexTextComponent.newBuilder("7 Things to Know for Today")
                .setGravity(FlexMessageComponent.Gravity.TOP)
                .setSize(FlexMessageComponent.Size.XS)
                .setFlex(1)
                .build();
        FlexTextComponent text2 = FlexTextComponent.newBuilder("Hay fever goes wild")
                .setGravity(FlexMessageComponent.Gravity.CENTER)
                .setSize(FlexMessageComponent.Size.XS)
                .setFlex(2)
                .build();
        FlexTextComponent text3 = FlexTextComponent.newBuilder("LINE Pay Begins Barcode Payment Service")
                .setGravity(FlexMessageComponent.Gravity.CENTER)
                .setSize(FlexMessageComponent.Size.XS)
                .setFlex(2)
                .build();
        FlexTextComponent text4 = FlexTextComponent.newBuilder("LINE Adds LINE Wallet")
                .setGravity(FlexMessageComponent.Gravity.BOTTOM)
                .setSize(FlexMessageComponent.Size.XS)
                .setFlex(1)
                .build();

        return FlexBoxComponent.newBuilder(FlexMessageComponent.Layout.VERTICAL,
                Arrays.asList(text1,
                        separatorComponent,
                        text2,
                        separatorComponent,
                        text3,
                        separatorComponent,
                        text4))
                .setFlex(2)
                .build();
    }

    @NonNull
    private FlexImageComponent createImageComponentForHeroField() {
        return FlexImageComponent
                .newBuilder("https://scdn.line-apps.com/n/channel_devcenter/img/fx/01_4_news.png")
                .setSize(FlexMessageComponent.Size.FULL)
                .setAspectRatio(FlexMessageComponent.AspectRatio.RATIO_20x13)
                .setAspectMode(FlexMessageComponent.AspectMode.COVER)
                .setAction(new UriAction("http://linecorp.com/"))
                .build();
    }

    @NonNull
    private FlexBoxComponent createBoxComponentForHeaderField() {
        FlexTextComponent headerTextComponent = FlexTextComponent.newBuilder("NEWS DIGEST")
                .setWeight(FlexMessageComponent.Weight.BOLD)
                .setColor("#aaaaaa")
                .setSize(FlexMessageComponent.Size.SM)
                .build();
        return FlexBoxComponent.newBuilder(
                FlexMessageComponent.Layout.HORIZONTAL, Arrays.asList(headerTextComponent))
                .build();
    }
}
