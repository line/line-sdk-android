<?cs def:custom_masthead() ?>
<div id="header">
    <div id="headerLeft">
      <div class="d-flex align-items-center">
        <a id="masthead-title" href="https://developers.line.biz/en/reference/android-sdk/">
          LINE SDK Docs
        </a>
        <nav class="GlobalHeaderNav">
          <ul>
            <li><a class="GlobalHeaderNavLink Link-blank" href="/en/" target="_blank" data-navlink="docs_site" data-translate="translate_header_menu_docs_site">LINE Developers Site</a></li>
            <li><a class="GlobalHeaderNavLink Link-blank" href="https://github.com/line/line-sdk-android/releases" target="_blank">Releases</a></li>
          </ul>
        </nav>
      </div>
    <!--
    <?cs if:project.name ?>
      <span id="masthead-title"><?cs var:project.name ?></span>
    <?cs /if ?>
    -->
    </div>
    <div id="headerRight">
      <?cs call:default_search_box() ?>
      <?cs if:reference && reference.apilevels ?>
        <?cs call:default_api_filter() ?>
      <?cs /if ?>
    </div>
</div><!-- header -->
<?cs /def ?>
