<template>
<div>
  <v-skeleton-loader type="list-item-two-line" />
  <div class="inline pa-3">
    <span class="subtitle-2">Supporters: </span>
    <v-chip v-for="(user, index) in request.supporters" :key="`s-${index}`"
      class="mx-1" label small
      :outlined="isPending(user)"
      :color="getColor(user)">
      <v-avatar left><v-icon>mdi-account</v-icon></v-avatar>
      {{user.name}}: {{user.status}}
    </v-chip>
  </div>

  <div class="inline pa-3">
    <span class="subtitle-2">Approvers: </span>
    <v-chip v-for="(user, index) in request.approvers" :key="`s-${index}`"
      class="mx-1" label small
      :outlined="isPending(user)"
      :color="getColor(user)">
      <v-avatar left><v-icon>mdi-account</v-icon></v-avatar>
      {{user.name}}: {{user.status}}
    </v-chip>
  </div>

  <div v-if="request.manual" class="pa-3">{{request.manual}}</div>
  <v-btn class="ma-2" outlined small color="indigo"
    :to="`/page/requests/${request.id}`">
      <span>See more details...</span>
  </v-btn>
</div>
</template>

<script>

  export default {
    props: {
      request: {
        type: Object,
        required: true,
      }
    },
    methods: {
      isPending(user) {
        return user.status==="PENDING";
      },
      getColor(user) {
        if(user.status==="REJECTED")
          return "deep-orange darken-3 white--text";
        else if(user.status==="APPROVED")
          return "info"
        else
          return "";
      },
    },
  };
</script>

<style scoped>
  
</style>
