<template>
  <div>
    <h3>{{user.name}}</h3>
    <v-select
      v-model="user.roles"
      :items="roles"
      :readonly="readonly"
      :chips="!readonly"
      label="Roles"
      multiple
    ></v-select>
  </div>
</template>

<script>

// import { mapFields } from 'vuex-map-fields';

export default {
  props: {
    user: {
      type: Object,
      required: true,
    },
    readonly: {
      type: Boolean,
      default: false,
    }
  },
  data: () => ({
    roles: ['Researcher', 'Devops01', 'Analyst'],
  }),
  // computed: {
  //   ...mapFields([
  //     'requestForm.effectiveDate',
  //     'requestForm.expiryDate',
  //     'requestForm.comments',
  //   ]),
  // },
};
</script>

<style scoped>
  
</style>
