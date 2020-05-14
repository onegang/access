<template>
  <div class="home">
    <div>
    </div>
    <UsersTable msg="Welcome to My Access"/>
  </div>
</template>

<script>
// @ is an alias to /src
import UsersTable from '../components/UsersTable.vue';

export default {
  name: 'Home',
  components: {
    UsersTable,
  },
};
</script>

<style scoped>
</style>
