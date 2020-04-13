<?cs def:custom_masthead() ?>
<div id="header">
    <div id="headerLeft">
      <div class="d-flex align-items-center">
        <a id="masthead-title" href="https://developers.line.biz/en/reference/">
          LINE SDK Docs
        </a>
        <nav class="GlobalHeaderNav">
          <ul>
            <li><a class="GlobalHeaderNavLink" href="/en/services/" data-navlink="services" data-translate="translate_header_menu_service">Products</a></li>
            <li><a class="GlobalHeaderNavLink" href="/en/docs/" data-navlink="docs" data-translate="translate_header_menu_documents">Documents</a></li>
            <li><a class="GlobalHeaderNavLink" href="/en/news/" data-navlink="news" data-translate="translate_header_menu_news">News</a></li>
            <li><a class="GlobalHeaderNavLink" href="/en/faq/" data-navlink="faq" data-translate="translate_header_menu_faq">FAQ</a></li>
            <li><a class="GlobalHeaderNavLink Link-blank" href="https://www.line-community.me/" target="_blank" data-translate="translate_header_menu_community">Community</a></li>
            <li><a class="GlobalHeaderNavLink Link-blank" href="https://engineering.linecorp.com/en/blog/" target="_blank" data-translate="translate_header_menu_blog">Blog</a></li>
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
