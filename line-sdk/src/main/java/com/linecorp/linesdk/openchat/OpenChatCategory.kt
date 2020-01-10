package com.linecorp.linesdk.openchat


enum class OpenChatCategory(
    val id: Int,
    val defaultString: String = ""
) {
    NotSelected(1, "Unselected (not shown in any category)"),
    School(2, "Schools"),
    Friend(7, "Friends"),
    Company(5, "Company"),
    Organization(6, "Organizations"),
    Region(8, "Local"),
    Baby(28, "Kids"),
    Sports(16, "Sports"),
    Game(17, "Games"),
    Book(29, "Books"),
    Movies(30, "Movies"),
    Photo(37, "Photos"),
    Art(41, "Art"),
    Animation(22, "Animation & comics"),
    Music(33, "Music"),
    Tv(24, "TV shows"),
    Celebrity(26, "Famous people"),
    Food(12, "Food"),
    Travel(18, "Travel"),
    Pet(27, "Pets"),
    Car(19, "Automotive"),
    Fashion(20, "Fashion & beauty"),
    Health(23, "Health"),
    Finance(40, "Finance & business"),
    Study(11, "Study"),
    Etc(35, "Other");
}
