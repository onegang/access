<template>
  <v-app>
    <v-content>
      <v-container>
        <v-tabs right>
          <v-tab to="/">Home</v-tab>
          <v-tab to="/requests">My Requests</v-tab>
          <v-tab to="/about">About</v-tab>
        </v-tabs>
        <router-view class="pt-10"></router-view>
      </v-container>
    </v-content>
  </v-app>
</template>

<style>


</style>
