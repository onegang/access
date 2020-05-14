<template>
  <div class="container app-access">
    <div class="nav nav-pills justify-content-center">
      <div class="nav-item">
        <router-link class="nav-link" active-class="active" to="/" exact>Home</router-link>
      </div>
      <div class="nav-item">
        <router-link class="nav-link" active-class="active" to="/requests">My Requests</router-link>
      </div>
      <div class="nav-item">
        <router-link class="nav-link" active-class="active" to="/about">About</router-link>
      </div>
    </div>
    <router-view/>
  </div>
</template>

<style>

.app-access {
  padding-top: 5px;
}

.nav-pills > div > a.active {
    background-color: black !important;
}

@import'~bootstrap/dist/css/bootstrap.css'
</style>
