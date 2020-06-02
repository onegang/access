<template>
  <v-app>
    <v-content>
      <v-container>
        <v-tabs right>
          <v-tab to="/">Home</v-tab>
          <v-tab to="/page/requests">My Requests</v-tab>
          <v-tab to="/page/about">About</v-tab>
        </v-tabs>
        <router-view class="pt-3"></router-view>
        <ErrorDialog />
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
import ErrorDialog from './components/utils/ErrorDialog.vue';

export default {
  components: {
    ErrorDialog,
  },
};
</script>

<style>


</style>
