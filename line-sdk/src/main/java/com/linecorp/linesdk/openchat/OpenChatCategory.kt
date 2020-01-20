package com.linecorp.linesdk.openchat

import com.linecorp.linesdk.R


enum class OpenChatCategory(
    val id: Int,
    val resourceId: Int
) {
    NotSelected(1, R.string.square_create_category_notselected),
    School(2, R.string.square_create_category_school),
    Friend(7, R.string.square_create_category_friend),
    Company(5, R.string.square_create_category_company),
    Organization(6, R.string.square_create_category_org),
    Region(8, R.string.square_create_category_region),
    Baby(28, R.string.square_create_category_baby),
    Sports(16, R.string.square_create_category_sports),
    Game(17, R.string.square_create_category_game),
    Book(29, R.string.square_create_category_book),
    Movies(30, R.string.square_create_category_movies),
    Photo(37, R.string.square_create_category_photo),
    Art(41, R.string.square_create_category_art),
    Animation(22, R.string.square_create_category_ani),
    Music(33, R.string.square_create_category_music),
    Tv(24, R.string.square_create_category_tv),
    Celebrity(26, R.string.square_create_category_celebrity),
    Food(12, R.string.square_create_category_food),
    Travel(18, R.string.square_create_category_travel),
    Pet(27, R.string.square_create_category_pet),
    Car(19, R.string.square_create_category_car),
    Fashion(20, R.string.square_create_category_fashion),
    Health(23, R.string.square_create_category_health),
    Finance(40, R.string.square_create_category_finance),
    Study(11, R.string.square_create_category_study),
    Etc(35, R.string.square_create_category_etc);
}
