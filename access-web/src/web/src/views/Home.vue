<template>
  <div class="home">
    <button type="button" class="btn btn-primary">New Request</button>
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
