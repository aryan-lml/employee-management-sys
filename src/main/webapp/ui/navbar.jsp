<%-- Nav bar includes a checkbox toggle for mobile sidebar (CSS-only) --%>
<input type="checkbox" id="nav-toggle" class="nav-toggle" aria-hidden="true">
<header class="navbar" role="banner">
  <div class="navbar-inner">
    <div class="nav-left">
      <label for="nav-toggle" class="hamburger" title="Toggle menu" aria-hidden="false">☰</label>
      <div class="nav-title">Welcome, <strong>Admin</strong></div>
      <div style="margin-left:12px;color:var(--muted);font-size:13px">Dashboard</div>
    </div>

    <div class="nav-right">
      <div class="top-controls">
        <a href="#" class="btn-ghost">Notifications</a>
        <a href="#" class="btn-ghost">Settings</a>
        <a href="/logout" class="btn-logout">Logout</a>
      </div>
    </div>
  </div>
</header>