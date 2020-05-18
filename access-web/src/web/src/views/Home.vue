<template>
  <div class="home">
    <div>
    </div>
    <v-badge
        :content="numberOfSelected"
        :value="numberOfSelected"
        overlap
        offset-x="25"
        offset-y="25"
      >
      <v-btn class="ma-2" outlined color="primary" :disabled="disabled"
        @click="toAccessForm">New Request</v-btn>
    </v-badge>
    <UsersTable msg="Welcome to My Access"/>
  </div>
</template>

<script>
// @ is an alias to /src
import UsersTable from '../components/UsersTable.vue';
import {mapGetters} from 'vuex';

export default {
  name: 'Home',
  components: {
    UsersTable,
  },
  computed: {
    numberOfSelected() {
      return this.$store.getters.USERS.filter(user => user.selected).length;
    },
    disabled() {
      return this.numberOfSelected===0;
    },
    ...mapGetters(['USERS']),
  },
  methods: {
    toAccessForm() {
      console.log('here');
    }
  },
};
</script>

<style scoped>
</style>
