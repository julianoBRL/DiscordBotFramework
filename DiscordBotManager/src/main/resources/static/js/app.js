(function () {
      'use strict';

      var body = document.body;
      var sidebar = document.getElementById('sidebar');
      var btnTop = document.getElementById('btnSidebarToggleTop');
      var yearEls = document.querySelectorAll('#year');
      var overlay;

      function setYear() {
        var y = new Date().getFullYear();
        yearEls.forEach(function (el) {
          el.textContent = y;
        });
      }

      // create overlay element (once)
      function ensureOverlay() {
        if (overlay) return overlay;
        overlay = document.createElement('div');
        overlay.className = 'sidebar-overlay';
        overlay.id = 'sidebarOverlay';
        document.body.appendChild(overlay);
        overlay.addEventListener('click', function () {
          hideMobileSidebar();
        });
        return overlay;
      }

      function isMobile() {
        return window.innerWidth < 992;
      }

      function isCollapsed() {
        return body.classList.contains('sidebar-collapsed');
      }

      function setCollapsed(state) {
        if (state) {
          body.classList.add('sidebar-collapsed');
          try {
            localStorage.setItem('sidebarCollapsed', '1');
          } catch (e) {}
        } else {
          body.classList.remove('sidebar-collapsed');
          try {
            localStorage.setItem('sidebarCollapsed', '0');
          } catch (e) {}
        }
      }

      function showMobileSidebar() {
        ensureOverlay();
        // ensure collapsed state doesn't interfere
        body.classList.remove('sidebar-collapsed');
        overlay.classList.add('show');
        sidebar.classList.add('show');
        // prevent body scroll while sidebar open on mobile
        document.documentElement.style.overflow = 'hidden';
      }

      function hideMobileSidebar() {
        if (overlay) overlay.classList.remove('show');
        sidebar.classList.remove('show');
        document.documentElement.style.overflow = '';
      }

      function hideNavbarCollapse() {
        var topNavEl = document.getElementById('topNav');
        if (!topNavEl) return;
        // if bootstrap collapse instance exists, hide it
        var inst = bootstrap.Collapse.getInstance(topNavEl);
        if (inst) {
          inst.hide();
        } else {
          // create then hide (safe)
          try {
            var c = new bootstrap.Collapse(topNavEl, { toggle: false });
            c.hide();
          } catch (e) { }
        }
      }

      function toggleSidebar() {
        if (isMobile()) {
          // close navbar collapsed menu first (avoids overlapping UI)
          hideNavbarCollapse();
          if (sidebar.classList.contains('show')) {
            hideMobileSidebar();
          } else {
            showMobileSidebar();
          }
        } else {
          setCollapsed(!isCollapsed());
        }
      }

      // attach event
      if (btnTop) {
        btnTop.addEventListener('click', function (e) {
          e.preventDefault();
          toggleSidebar();
        });
      }

      // close mobile sidebar when clicking outside (overlay handles), and when resizing to desktop
      window.addEventListener('resize', function () {
        if (!isMobile()) {
          if (overlay) overlay.classList.remove('show');
          sidebar.classList.remove('show');
          document.documentElement.style.overflow = '';

          try {
            var pref = localStorage.getItem('sidebarCollapsed');
            if (pref === '1') {
              body.classList.add('sidebar-collapsed');
            } else {
              body.classList.remove('sidebar-collapsed');
            }
          } catch (e) {}
        } else {
          // ensure collapsed class doesn't hide off-canvas on mobile
          body.classList.remove('sidebar-collapsed');
        }
      });

      // initialize collapsed state based on preference (desktop only)
      try {
        var pref = localStorage.getItem('sidebarCollapsed');
        if (pref === '1' && !isMobile()) {
          body.classList.add('sidebar-collapsed');
        }
      } catch (e) {}

      // close mobile sidebar when pressing ESC
      document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && isMobile() && sidebar.classList.contains('show')) {
          hideMobileSidebar();
        }
      });

      // Portal behavior for dropdown menus: keep existing logic but avoid moving bot-card menus off when causing stacking issues.
      document.addEventListener('show.bs.dropdown', function (ev) {
        try {
          var dropdownEl = ev.target;
          if (!dropdownEl || dropdownEl.nodeType !== 1) return;
          var menu = dropdownEl.querySelector('.dropdown-menu');
          var toggler = dropdownEl.querySelector('[data-bs-toggle="dropdown"], .dropdown-toggle');
          if (!menu || !toggler) return;

          var inSidebar = dropdownEl.closest('#sidebar') !== null;
          var inBotCard = dropdownEl.closest('.bot-card') !== null;

          // Only portal sidebar footer menu when collapsed on desktop.
          if (inSidebar && body.classList.contains('sidebar-collapsed') && window.innerWidth >= 992) {
            if (!menu.__origParent) {
              menu.__origParent = menu.parentElement;
              menu.__origNext = menu.nextSibling;
            }
            menu.classList.add('dropdown-menu-portal');
            document.body.appendChild(menu);

            requestAnimationFrame(function () {
              var rect = toggler.getBoundingClientRect();
              var left = rect.right + 8;
              var top = rect.top + (rect.height / 2) - (menu.offsetHeight / 2);
              var viewportW = Math.max(document.documentElement.clientWidth || 0, window.innerWidth || 0);
              if (left + menu.offsetWidth > viewportW - 8) {
                left = Math.max(8, rect.left - menu.offsetWidth - 8);
              }
              var viewportH = Math.max(document.documentElement.clientHeight || 0, window.innerHeight || 0);
              if (top + menu.offsetHeight > viewportH - 8) {
                top = Math.max(8, viewportH - menu.offsetHeight - 8);
              }
              if (top < 8) top = 8;
              menu.style.position = 'fixed';
              menu.style.left = left + 'px';
              menu.style.top = top + 'px';
              menu.style.zIndex = 4050;
            });
          } else {
            // For bot-card menus and others, do NOT portal — let bootstrap handle stacking normally.
            // Ensure menu has high z-index so it appears above elevated cards.
            menu.style.zIndex = 4100;
          }
        } catch (e) {
          // ignore
        }
      });

      document.addEventListener('hide.bs.dropdown', function (ev) {
        try {
          // restore portal menus back to original parent
          var portalMenus = document.querySelectorAll('.dropdown-menu-portal');
          portalMenus.forEach(function (menu) {
            if (menu.__origParent) {
              if (menu.__origNext && menu.__origNext.parentNode === menu.__origParent) {
                menu.__origParent.insertBefore(menu, menu.__origNext);
              } else {
                menu.__origParent.appendChild(menu);
              }
              menu.classList.remove('dropdown-menu-portal');
              menu.style.position = '';
              menu.style.left = '';
              menu.style.top = '';
              menu.style.zIndex = '';
              delete menu.__origParent;
              delete menu.__origNext;
            }
          });
        } catch (e) {
          // ignore
        }
      });

      setYear();
    })();
